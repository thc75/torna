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
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.json.JsonUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.service.DocInfoService;
import cn.torna.service.ModuleConfigService;
import cn.torna.service.dto.DocInfoDTO;
import com.alibaba.fastjson.JSON;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author tanghc
 */
@ApiService
@ApiDoc(value = "文档API", order = 1)
@Slf4j
public class DocApi {

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private ModuleConfigService moduleConfigService;

    @Api(name = "doc.push")
    @ApiDocMethod(description = "推送文档",  order = 0, remark = "把第三方文档推送给Torna服务器")
    @Transactional(rollbackFor = Exception.class)
    public void pushDoc(DocPushParam param) {
        ApiParam apiParam = ApiContext.getApiParam();
        log.debug("推送文档, appKey:{}, token:{}, 推送内容：\n{}",
                apiParam.fatchAppKey(),
                apiParam.fatchAccessToken(),
                JSON.toJSONString(param)
        );
        long moduleId = RequestContext.getCurrentContext().getModuleId();
        // 设置调试环境
        for (DebugEnvParam debugEnv : param.getDebugEnvs()) {
            moduleConfigService.setDebugEnv(moduleId, debugEnv.getName(), debugEnv.getUrl());
        }
        // 先删除之前的文档
//        User user = RequestContext.getCurrentContext().getApiUser();
//        docInfoService.deleteModuleDocs(moduleId, user.getUserId());
        for (DocPushItemParam detailPushParam : param.getApis()) {
            this.pushDocItem(detailPushParam);
        }
    }

    public void pushDocItem(DocPushItemParam param) {
        User user = RequestContext.getCurrentContext().getApiUser();
        long moduleId = RequestContext.getCurrentContext().getModuleId();
        DocInfoDTO docInfoDTO = JsonUtil.parseObject(JsonUtil.toJSONString(param), DocInfoDTO.class);
        docInfoDTO.setModuleId(moduleId);
        if (Booleans.isTrue(param.getIsFolder())) {
            DocInfo folder = docInfoService.createDocFolder(param.getName(), moduleId, user);
            List<DocPushItemParam> items = param.getItems();
            if (items != null) {
                for (DocPushItemParam item : items) {
                    item.setParentId(folder.getId());
                    this.pushDocItem(item);
                }
            }
        } else {
            docInfoService.saveDocInfo(docInfoDTO, user);
        }
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
