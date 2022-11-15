package cn.torna.api.open;

import cn.torna.api.bean.ApiUser;
import cn.torna.api.bean.PushContext;
import cn.torna.api.bean.RequestContext;
import cn.torna.api.open.param.CategoryAddParam;
import cn.torna.api.open.param.CategoryUpdateParam;
import cn.torna.api.open.param.CodeParamPushParam;
import cn.torna.api.open.param.DebugEnvParam;
import cn.torna.api.open.param.DocPushItemParam;
import cn.torna.api.open.param.DocPushParam;
import cn.torna.api.open.param.DubboParam;
import cn.torna.api.open.result.DocCategoryResult;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.bean.User;
import cn.torna.common.enums.DocTypeEnum;
import cn.torna.common.enums.UserSubscribeTypeEnum;
import cn.torna.common.message.MessageEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.ThreadPoolUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.DocParam;
import cn.torna.dao.entity.Module;
import cn.torna.manager.tx.TornaTransactionManager;
import cn.torna.service.DocInfoService;
import cn.torna.service.ModuleConfigService;
import cn.torna.service.ModuleEnvironmentService;
import cn.torna.service.UserMessageService;
import cn.torna.service.dto.DocFolderCreateDTO;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocMeta;
import cn.torna.service.dto.DocParamDTO;
import cn.torna.service.dto.MessageDTO;
import cn.torna.service.dto.UpdateDocFolderDTO;
import com.alibaba.fastjson.JSON;
import com.gitee.easyopen.ApiContext;
import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import com.gitee.easyopen.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
    private static final char SPLIT = '/';
    private static final String PREFIX = "://";
    private static final String PUSH_ERROR_MSG = "【%s】推送失败，请查看日志";

    private final Object lock = new Object();

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private ModuleConfigService moduleConfigService;

    @Autowired
    private ModuleEnvironmentService moduleEnvironmentService;

    @Autowired
    private TornaTransactionManager tornaTransactionManager;

    @Autowired
    private UserMessageService userMessageService;


    @Api(name = "doc.push")
    @ApiDocMethod(description = "推送文档", order = 0, remark = "把第三方文档推送给Torna服务器")
    public void pushDoc(DocPushParam param) {
        // 是否打印推送内容
        String isPrint = EnvironmentKeys.TORNA_PUSH_PRINT_CONTENT.getValue();
        if (Boolean.parseBoolean(isPrint)) {
            log.info("推送内容：\n{}", JSON.toJSONString(param));
        }
        // 允许有相同的目录
        String allowSameFolder = EnvironmentKeys.TORNA_PUSH_ALLOW_SAME_FOLDER.getValue();
        if (Boolean.parseBoolean(allowSameFolder)) {
            this.mergeSameFolder(param);
        } else {
            this.checkSameFolder(param);
        }
        RequestContext context = RequestContext.getCurrentContext();
        String author = param.getAuthor();
        if (StringUtils.hasText(author)) {
            ApiUser user = (ApiUser) context.getApiUser();
            user.setNickname(author);
        }
        ThreadPoolUtil.execute(() -> doPush(param, context));
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
     *
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

    private void doPush(DocPushParam param, RequestContext context) {
        String token = context.getToken();
        Module module = context.getModule();
        long moduleId = module.getId();
        String ip = context.getIp();
        log.info("【PUSH】收到文档推送，模块名称：{}，推送人：{}，ip：{}，token：{}", module.getName(), param.getAuthor(), ip, token);
        long startTime = System.currentTimeMillis();
        List<DocMeta> docMetas = docInfoService.listDocMeta(moduleId);
        PushContext pushContext = new PushContext(docMetas, new ArrayList<>());
        ThreadLocal<DocPushItemParam> docPushItemParamThreadLocal = new ThreadLocal<>();
        synchronized (lock) {
            tornaTransactionManager.execute(() -> {
                // 设置调试环境
                for (DebugEnvParam debugEnv : param.getDebugEnvs()) {
                    if (StringUtils.isEmpty(debugEnv.getName()) || StringUtils.isEmpty(debugEnv.getUrl())) {
                        continue;
                    }
                    moduleEnvironmentService.setDebugEnv(moduleId, debugEnv.getName(), debugEnv.getUrl());
                }
                // 替换文档
                if (Booleans.isTrue(param.getIsReplace(), true) && !BooleanUtils.toBoolean(param.getIsOverride())) {
                    // 先删除之前的文档
                    this.deleteOpenAPIModuleDocs(moduleId);
                }
                for (DocPushItemParam detailPushParam : param.getApis()) {
                    docPushItemParamThreadLocal.set(detailPushParam);
                    this.pushDocItem(detailPushParam, context, 0L, pushContext, param);
                }
                // 设置公共错误码
                this.setCommonErrorCodes(moduleId, param.getCommonErrorCodes());
                return null;
            }, e -> {
                DocPushItemParam docPushItemParam = docPushItemParamThreadLocal.get();
                String paramInfo = JSON.toJSONString(docPushItemParam);
                log.error("【PUSH】保存文档失败，模块名称：{}，推送人：{}，ip：{}，token：{}, 文档信息：{}", module.getName(), param.getAuthor(), ip, token, paramInfo, e);
                this.sendMessage(String.format(PUSH_ERROR_MSG, docPushItemParam.getName()));
            });
            log.info("【PUSH】推送处理完成，模块名称：{}，推送人：{}，ip：{}，token：{}，耗时：{}秒",
                    module.getName(), param.getAuthor(), ip, token, (System.currentTimeMillis() - startTime)/1000.0);
        }
    }

    /**
     * 删除之前的文档
     *
     * @param moduleId moduleId
     */
    private void deleteOpenAPIModuleDocs(long moduleId) {
        docInfoService.deleteOpenAPIModuleDocs(moduleId);
    }

    private void sendMessage(String msg) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageEnum(MessageEnum.SYSTEM_ERROR);
        messageDTO.setType(UserSubscribeTypeEnum.PUSH_DOC);
        messageDTO.setLocale(ApiContext.getLocal());
        messageDTO.setSourceId(0L);
        userMessageService.sendMessageToAdmin(messageDTO, msg);
    }

    public void pushDocItem(DocPushItemParam param, RequestContext context, Long parentId, PushContext pushContext, DocPushParam docPushParam) {
        User user = context.getApiUser();
        long moduleId = context.getModuleId();
        List<DocMeta> docMetas = pushContext.getDocMetas();
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
            // 被锁住
            if (DocInfoService.isLocked(docFolderCreateDTO.buildDataId(), docMetas)) {
                return;
            }
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
                    this.pushDocItem(item, context, pid, pushContext, docPushParam);
                }
            }
        } else {
            DocInfoDTO docInfoDTO = buildDocInfoDTO(param);
            docInfoDTO.setModuleId(moduleId);
            docInfoDTO.setParentId(parentId);
            formatUrl(docInfoDTO);
            // 被锁住
            if (DocInfoService.isLocked(docInfoDTO.buildDataId(), docMetas)) {
                return;
            }
            docInfoService.doPushSaveDocInfo(docInfoDTO, user, BooleanUtils.toBoolean(docPushParam.getIsOverride()));
        }
    }


    private void setCommonErrorCodes(long moduleId, List<CodeParamPushParam> commonErrorCodes) {
        if (CollectionUtils.isEmpty(commonErrorCodes)) {
            return;
        }
        List<DocParam> errorCodeParams = CopyUtil.copyList(commonErrorCodes, DocParam::new);
        moduleConfigService.setCommonErrorCodeList(errorCodeParams, moduleId);
    }

    private static DocInfoDTO buildDocInfoDTO(DocPushItemParam param) {
        DocInfoDTO docInfoDTO = CopyUtil.deepCopy(param, DocInfoDTO.class);
        List<CodeParamPushParam> errorCodeParams = param.getErrorCodeParams();
        if (!CollectionUtils.isEmpty(errorCodeParams)) {
            List<DocParamDTO> errorParams = CopyUtil.copyList(errorCodeParams, DocParamDTO::new);
            docInfoDTO.setErrorCodeParams(errorParams);
        }
        if (StringUtils.hasText(param.getDefinition())) {
            docInfoDTO.setUrl(param.getDefinition());
        }
        String md5 = DocInfoService.getDocMd5(docInfoDTO);
        docInfoDTO.setMd5(md5);
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
        String urlLowerCase = url.toLowerCase();
        if (urlLowerCase.startsWith(HTTP) || urlLowerCase.startsWith(HTTPS)) {
            int prefixIndex = urlLowerCase.indexOf(PREFIX);
            url = url.substring(prefixIndex + PREFIX.length());
            url = StringUtils.trimLeadingCharacter(url, SPLIT);
            int index = url.indexOf(SPLIT);
            if (index > 0) {
                url = url.substring(index);
            }
        }
        return SPLIT + StringUtils.trimLeadingCharacter(url, SPLIT);
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
        UpdateDocFolderDTO updateDocFolderDTO = new UpdateDocFolderDTO();
        updateDocFolderDTO.setId(param.getId());
        updateDocFolderDTO.setName(name);
        updateDocFolderDTO.setUser(user);
        updateDocFolderDTO.setParentId(0L);
        docInfoService.updateDocFolderName(updateDocFolderDTO);
    }


}
