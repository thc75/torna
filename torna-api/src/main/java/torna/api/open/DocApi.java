package torna.api.open;

import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.NoResultWrapper;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import org.springframework.beans.factory.annotation.Autowired;
import torna.api.bean.RequestContext;
import torna.api.open.param.DocInfoDetailCreateParam;
import torna.api.open.param.DocInfoDetailUpdateParam;
import torna.api.open.param.CategoryAddParam;
import torna.api.open.param.CategoryUpdateParam;
import torna.api.open.param.IdParam;
import torna.api.open.result.DocCategoryResult;
import torna.api.open.result.DocInfoDetailResult;
import torna.api.open.result.DocInfoResult;
import torna.api.open.result.DocSaveResult;
import torna.common.bean.User;
import torna.common.util.CopyUtil;
import torna.dao.entity.DocInfo;
import torna.service.DocInfoService;
import torna.service.dto.DocInfoDTO;

import java.util.List;

/**
 * @author tanghc
 */
@ApiService
@ApiDoc(value = "文档API")
public class DocApi {

    @Autowired
    private DocInfoService docInfoService;

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

    @Api(name = "doc.create")
    @ApiDocMethod(description = "创建文档", order = 6)
    public DocSaveResult addDoc(DocInfoDetailCreateParam param) {
        User user = RequestContext.getCurrentContext().getApiUser();
        DocInfoDTO docInfoDTO = CopyUtil.copyBean(param, DocInfoDTO::new);
        DocInfo docInfo = docInfoService.saveDocInfo(docInfoDTO, user);
        return CopyUtil.copyBean(docInfo, DocSaveResult::new);
    }

    @Api(name = "doc.update")
    @ApiDocMethod(description = "修改文档", order = 6)
    public DocSaveResult updateDoc(DocInfoDetailUpdateParam param) {
        User user = RequestContext.getCurrentContext().getApiUser();
        DocInfoDTO docInfoDTO = CopyUtil.copyBean(param, DocInfoDTO::new);
        DocInfo docInfo = docInfoService.saveDocInfo(docInfoDTO, user);
        return CopyUtil.copyBean(docInfo, DocSaveResult::new);
    }

}
