package cn.torna.api.open;

import cn.torna.api.bean.RequestContext;
import cn.torna.api.open.param.CategoryAddParam;
import cn.torna.api.open.param.CategoryUpdateParam;
import cn.torna.api.open.param.DebugEnvParam;
import cn.torna.api.open.param.DocPushItemParam;
import cn.torna.api.open.param.DocPushParam;
import cn.torna.api.open.param.IdParam;
import cn.torna.api.open.result.DocCategoryResult;
import cn.torna.api.open.result.DocInfoDetailResult;
import cn.torna.api.open.result.DocInfoResult;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.User;
import cn.torna.common.enums.UserSubscribeTypeEnum;
import cn.torna.common.message.MessageEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.ThreadPoolUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.manager.tx.TornaTransactionManager;
import cn.torna.service.DocInfoService;
import cn.torna.service.ModuleConfigService;
import cn.torna.service.UserMessageService;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.MessageDTO;
import com.gitee.easyopen.ApiContext;
import com.gitee.easyopen.ApiParam;
import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.NoResultWrapper;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author tanghc
 */
@ApiService
@ApiDoc(value = "文档API", order = 1)
@Slf4j
public class DocApi {

    private static final String HTTP = "http:";
    private static final String HTTPS = "https:";

    private final Object lock = new Object();

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private ModuleConfigService moduleConfigService;

    @Autowired
    private TornaTransactionManager tornaTransactionManager;

    @Autowired
    private UserMessageService userMessageService;

    @Api(name = "doc.push")
    @ApiDocMethod(description = "推送文档", order = 0, remark = "把第三方文档推送给Torna服务器")
    public void pushDoc(DocPushParam param) {
        ApiParam apiParam = ApiContext.getApiParam();
        RequestContext context = RequestContext.getCurrentContext();
        ThreadPoolUtil.execute(() -> doPush(param, apiParam, context));
    }

    private void doPush(DocPushParam param, ApiParam apiParam, RequestContext context) {
        String appKey = apiParam.fatchAppKey();
        String token = context.getToken();
        long moduleId = context.getModuleId();
        log.info("收到文档推送，appKey:{}, token:{}, moduleId:{}", appKey, token, moduleId);
        tornaTransactionManager.execute(() -> {
            // fix:MySQL多个session下insert on duplicate key update导致死锁问题
            // https://blog.csdn.net/li563868273/article/details/105213266/
            // 解决思路：只能有一个线程在执行，当一个线程处理完后，下一个线程再接着执行
            synchronized (lock) {
                // 设置调试环境
                for (DebugEnvParam debugEnv : param.getDebugEnvs()) {
                    moduleConfigService.setDebugEnv(moduleId, debugEnv.getName(), debugEnv.getUrl());
                }
                // 先删除之前的文档
                User user = context.getApiUser();
                docInfoService.deleteOpenAPIModuleDocs(moduleId, user.getUserId());
                for (DocPushItemParam detailPushParam : param.getApis()) {
                    this.pushDocItem(detailPushParam, context, 0L);
                }
            }
            return null;
        }, e -> {
            log.error("保存文档失败，appKey:{}, token:{}, moduleId:{}", appKey, token, moduleId, e);
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setMessageEnum(MessageEnum.SYSTEM_ERROR);
            messageDTO.setType(UserSubscribeTypeEnum.PUSH_DOC);
            messageDTO.setLocale(ApiContext.getLocal());
            messageDTO.setSourceId(0L);
            userMessageService.sendMessageToAdmin(messageDTO, e.getMessage());
        });
    }

    public void pushDocItem(DocPushItemParam param, RequestContext context, Long parentId) {
        User user = context.getApiUser();
        long moduleId = context.getModuleId();
        if (Booleans.isTrue(param.getIsFolder())) {
            DocInfo folder = docInfoService.createDocFolder(param.getName(), moduleId, user, parentId);
            List<DocPushItemParam> items = param.getItems();
            if (items != null) {
                for (DocPushItemParam item : items) {
                    Long pid = folder.getId();
                    this.pushDocItem(item, context, pid);
                }
            }
        } else {
            DocInfoDTO docInfoDTO = CopyUtil.deepCopy(param, DocInfoDTO.class);
            docInfoDTO.setModuleId(moduleId);
            docInfoDTO.setParentId(parentId);
            formatUrl(docInfoDTO);
            docInfoService.doSaveDocInfo(docInfoDTO, user);
        }
    }

    private static void formatUrl(DocInfoDTO docInfoDTO) {
        String url = docInfoDTO.getUrl();
        url = removePrefix(url);
        docInfoDTO.setUrl(url);
    }

    private static String removePrefix(String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        char split = '/';
        String urlLowerCase = url.toLowerCase();
        if (urlLowerCase.startsWith(HTTP)) {
            url = url.substring(HTTP.length());
            url = StringUtils.trimLeadingCharacter(url, split);
        } else if (urlLowerCase.startsWith(HTTPS)) {
            url = url.substring(HTTPS.length());
            url = StringUtils.trimLeadingCharacter(url, split);
        } else if (url.charAt(0) != split) {
            url = split + url;
        }
        int index = url.indexOf(split);
        if (index > 0) {
            url = url.substring(index);
        }
        return url;
    }

    @Api(name = "doc.list")
    @ApiDocMethod(description = "获取文档列表"
            , order = 1
            , wrapperClass = NoResultWrapper.class
            , results = {
            @ApiDocField(name = "code", description = "状态值，\"0\"表示成功，其它都是失败", example = "0"),
            @ApiDocField(name = "msg", description = "错误信息，出错时显示"),
            @ApiDocField(name = "data", description = "文档数组", elementClass = DocInfoResult.class)
    })
    public List<DocInfoResult> list() {
        long moduleId = RequestContext.getCurrentContext().getModuleId();
        List<DocInfo> docInfos = docInfoService.list("module_id", moduleId);
        return CopyUtil.copyList(docInfos, DocInfoResult::new);
    }

    @Api(name = "doc.get")
    @ApiDocMethod(description = "获取文档详情", order = 2)
    public DocInfoDetailResult getDoc(IdParam param) {
        DocInfoDTO docDetail = docInfoService.getDocDetail(param.getId());
        return CopyUtil.copyBean(docDetail, DocInfoDetailResult::new);
    }

    @Api(name = "doc.category.list")
    @ApiDocMethod(description = "获取分类"
            , order = 3
            , wrapperClass = NoResultWrapper.class
            , results = {
            @ApiDocField(name = "code", description = "状态值，\"0\"表示成功，其它都是失败", example = "0"),
            @ApiDocField(name = "msg", description = "错误信息，出错时显示"),
            @ApiDocField(name = "data", description = "分类数组", elementClass = DocInfoResult.class)
    })
    public List<DocCategoryResult> listFolders() {
        long moduleId = RequestContext.getCurrentContext().getModuleId();
        List<DocInfo> folders = docInfoService.listFolders(moduleId);
        return CopyUtil.copyList(folders, DocCategoryResult::new);
    }

    @Api(name = "doc.category.create")
    @ApiDocMethod(description = "创建分类", order = 4)
    public DocCategoryResult addCategory(CategoryAddParam param) {
        long moduleId = RequestContext.getCurrentContext().getModuleId();
        User apiUser = RequestContext.getCurrentContext().getApiUser();
        DocInfo category = docInfoService.createDocFolder(param.getName(), moduleId, apiUser);
        return CopyUtil.copyBean(category, DocCategoryResult::new);
    }

    @Api(name = "doc.category.name.update")
    @ApiDocMethod(description = "修改分类名称", order = 5)
    public void updateCategory(CategoryUpdateParam param) {
        String name = param.getName();
        User user = RequestContext.getCurrentContext().getApiUser();
        docInfoService.updateDocFolderName(param.getId(), name, user);
    }


}
