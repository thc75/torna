package cn.torna.api.open;

import cn.torna.api.bean.ApiUser;
import cn.torna.api.bean.RequestContext;
import cn.torna.api.open.param.CategoryAddParam;
import cn.torna.api.open.param.CategoryUpdateParam;
import cn.torna.api.open.param.CodeParamPushParam;
import cn.torna.api.open.param.DebugEnvParam;
import cn.torna.api.open.param.DocPushItemParam;
import cn.torna.api.open.param.DocPushParam;
import cn.torna.api.open.param.DubboParam;
import cn.torna.api.open.param.IdParam;
import cn.torna.api.open.result.DocCategoryResult;
import cn.torna.api.open.result.DocInfoDetailResult;
import cn.torna.api.open.result.DocInfoResult;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.User;
import cn.torna.common.enums.DocTypeEnum;
import cn.torna.common.enums.UserSubscribeTypeEnum;
import cn.torna.common.message.MessageEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.ThreadPoolUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.DocParam;
import cn.torna.manager.tx.TornaTransactionManager;
import cn.torna.service.DocInfoService;
import cn.torna.service.ModuleConfigService;
import cn.torna.service.UserMessageService;
import cn.torna.service.dto.DocFolderCreateDTO;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocParamDTO;
import cn.torna.service.dto.MessageDTO;
import com.alibaba.fastjson.JSON;
import com.gitee.easyopen.ApiContext;
import com.gitee.easyopen.ApiParam;
import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.NoResultWrapper;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import com.gitee.easyopen.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Value("${torna.push.allow-same-folder}")
    private boolean allowSameFolder;

    @Value("${torna.push.print-content}")
    private boolean watchPushContent;

    @Api(name = "doc.push")
    @ApiDocMethod(description = "推送文档", order = 0, remark = "把第三方文档推送给Torna服务器")
    public void pushDoc(DocPushParam param) {
        if (watchPushContent) {
            log.info("推送内容：\n{}", JSON.toJSONString(param));
        }
        if (allowSameFolder) {
            this.mergeSameFolder(param);
        } else {
            this.checkSameFolder(param);
        }
        ApiParam apiParam = ApiContext.getApiParam();
        RequestContext context = RequestContext.getCurrentContext();
        String author = param.getAuthor();
        if (StringUtils.hasText(author)) {
            ApiUser user = (ApiUser) context.getApiUser();
            user.setNickname(author);
        }
        ThreadPoolUtil.execute(() -> doPush(param, apiParam, context));
    }

    private void checkSameFolder(DocPushParam param) {
        List<DocPushItemParam> apis = param.getApis();
        if (CollectionUtils.isEmpty(apis)) {
            return;
        }
        // key:分类名称，value:相同文档数量
        Map<String, AtomicInteger> folderCount = new HashMap<>(8);
        for (DocPushItemParam api : apis) {
            if (api.getIsFolder() == Booleans.TRUE) {
                AtomicInteger count = folderCount.computeIfAbsent(api.getName(), (k) -> new AtomicInteger(0));
                count.incrementAndGet();
            }
        }
        folderCount.forEach((name, count) -> {
            if (count.get() > 1) {
                String msg = "文档名称重复【" + name + "】";
                this.sendMessage(msg);
                throw new ApiException(msg);
            }
        });
    }

    /**
     * 将相同的目录进行合并
     * @param param
     */
    private void mergeSameFolder(DocPushParam param) {
        List<DocPushItemParam> apis = param.getApis();
        if (CollectionUtils.isEmpty(apis)) {
            return;
        }
        // key:分类名称， value:分类下的文档
        Map<DocPushItemParam, List<DocPushItemParam>> folderItems = new LinkedHashMap<>(8);
        for (DocPushItemParam api : apis) {
            if (api.getIsFolder() == Booleans.TRUE) {
                List<DocPushItemParam> docItemList = folderItems.computeIfAbsent(api, (k) -> new ArrayList<>());
                docItemList.addAll(api.getItems());
            }
        }
        if (folderItems.isEmpty()) {
            return;
        }
        folderItems.forEach(DocPushItemParam::setItems);
        param.setApis(new ArrayList<>(folderItems.keySet()));
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
                    if (StringUtils.isEmpty(debugEnv.getName()) || StringUtils.isEmpty(debugEnv.getUrl())) {
                        continue;
                    }
                    moduleConfigService.setDebugEnv(moduleId, debugEnv.getName(), debugEnv.getUrl());
                }
                // 先删除之前的文档
                User user = context.getApiUser();
                docInfoService.deleteOpenAPIModuleDocs(moduleId, user.getUserId());
                for (DocPushItemParam detailPushParam : param.getApis()) {
                    this.pushDocItem(detailPushParam, context, 0L);
                }
                // 设置公共错误码
                this.setCommonErrorCodes(moduleId, param.getCommonErrorCodes());
            }
            return null;
        }, e -> {
            log.error("保存文档失败，appKey:{}, token:{}, moduleId:{}", appKey, token, moduleId, e);
            this.sendMessage(e.getMessage());
        });
    }

    private void sendMessage(String msg) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageEnum(MessageEnum.SYSTEM_ERROR);
        messageDTO.setType(UserSubscribeTypeEnum.PUSH_DOC);
        messageDTO.setLocale(ApiContext.getLocal());
        messageDTO.setSourceId(0L);
        userMessageService.sendMessageToAdmin(messageDTO, msg);
    }

    public void pushDocItem(DocPushItemParam param, RequestContext context, Long parentId) {
        User user = context.getApiUser();
        long moduleId = context.getModuleId();
        if (Booleans.isTrue(param.getIsFolder())) {
            DocInfo folder;
            Map<String, Object> props = null;
            DocTypeEnum docTypeEnum = param.getDubboInfo() != null ? DocTypeEnum.DUBBO : DocTypeEnum.HTTP;
            if (docTypeEnum == DocTypeEnum.DUBBO) {
                props = buildProps(param);
            }
            DocFolderCreateDTO docFolderCreateDTO = new DocFolderCreateDTO();
            docFolderCreateDTO.setName(param.getName());
            docFolderCreateDTO.setModuleId(moduleId);
            docFolderCreateDTO.setUser(user);
            docFolderCreateDTO.setParentId(parentId);
            docFolderCreateDTO.setDocTypeEnum(docTypeEnum);
            docFolderCreateDTO.setProps(props);
            docFolderCreateDTO.setAuthor(param.getAuthor());
            docFolderCreateDTO.setOrderIndex(param.getOrderIndex());
            folder = docInfoService.createDocFolder(docFolderCreateDTO);
            List<DocPushItemParam> items = param.getItems();
            if (items != null) {
                for (DocPushItemParam item : items) {
                    Long pid = folder.getId();
                    item.setType(docTypeEnum.getType());
                    // 如果接口作者为空，则使用文件夹作者
                    if (StringUtils.isEmpty(item.getAuthor())) {
                        item.setAuthor(folder.getAuthor());
                    }
                    this.pushDocItem(item, context, pid);
                }
            }
        } else {
            DocInfoDTO docInfoDTO = buildDocInfoDTO(param);
            docInfoDTO.setModuleId(moduleId);
            docInfoDTO.setParentId(parentId);
            formatUrl(docInfoDTO);
            docInfoService.doSaveDocInfo(docInfoDTO, user);
        }
    }

    private void setCommonErrorCodes(long moduleId, List<CodeParamPushParam> commonErrorCodes) {
        if (CollectionUtils.isEmpty(commonErrorCodes)) {
            return;
        }
        List<DocParam> errorCodeParams = CopyUtil.copyList(commonErrorCodes, DocParam::new);
        moduleConfigService.setCommonErrorCodes(errorCodeParams, moduleId);
    }

    private static DocInfoDTO buildDocInfoDTO(DocPushItemParam param) {
        DocInfoDTO docInfoDTO = CopyUtil.deepCopy(param, DocInfoDTO.class);
        List<CodeParamPushParam> errorCodeParams = param.getErrorCodeParams();
        if (CollectionUtils.isNotEmpty(errorCodeParams)) {
            List<DocParamDTO> errorParams = CopyUtil.copyList(errorCodeParams, DocParamDTO::new);
            docInfoDTO.setErrorCodeParams(errorParams);
        }
        if (StringUtils.hasText(param.getDefinition())) {
            docInfoDTO.setUrl(param.getDefinition());
        }
        return docInfoDTO;
    }

    private Map<String, Object> buildProps(DocPushItemParam param) {
        DubboParam dubboInfo = param.getDubboInfo();
        if (dubboInfo == null) {
            return Collections.emptyMap();
        }
        String json = JSON.toJSONString(dubboInfo);
        return JSON.parseObject(json);
    }

    private static void formatUrl(DocInfoDTO docInfoDTO) {
        if (docInfoDTO.getType() == DocTypeEnum.DUBBO.getType()) {
            return;
        }
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
