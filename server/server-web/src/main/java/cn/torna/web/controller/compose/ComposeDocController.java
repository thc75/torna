package cn.torna.web.controller.compose;

import cn.torna.common.annotation.HashId;
import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.enums.StatusEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.ComposeDoc;
import cn.torna.dao.entity.ComposeProject;
import cn.torna.dao.entity.DocInfo;
import cn.torna.service.ComposeDocService;
import cn.torna.service.ComposeProjectService;
import cn.torna.service.DocInfoService;
import cn.torna.web.controller.compose.param.ComposeDocAddParam;
import cn.torna.web.controller.compose.param.ComposeDocFolderAddParam;
import cn.torna.web.controller.compose.param.ComposeDocFolderUpdateParam;
import cn.torna.web.controller.compose.vo.ComposeDocVO;
import cn.torna.web.controller.doc.param.UpdateOrderIndexParam;
import cn.torna.web.controller.system.param.IdParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("compose/doc")
public class ComposeDocController {

    @Autowired
    private ComposeDocService composeDocService;

    @Autowired
    private ComposeProjectService composeProjectService;

    @Autowired
    private DocInfoService docInfoService;

    @PostMapping("add")
    public Result addDoc(@RequestBody ComposeDocAddParam param) {
        User user = UserContext.getUser();
        List<ComposeDocAddParam.Doc> docList = param.getDocList();
        if (CollectionUtils.isEmpty(docList)) {
            throw new BizException("请选择文档");
        }
        List<ComposeDoc> composeDocs = composeDocService.listByProjectIdAndParentId(param.getProjectId(), param.getParentId());
        // 过滤已经添加的docId
        docList = filterDocId(docList, composeDocs);
        if (CollectionUtils.isEmpty(docList)) {
            return Result.ok();
        }
        List<ComposeDoc> tobeSave = docList.stream()
                .map(doc -> buildComposeDoc(doc, param, user))
                .collect(Collectors.toList());
        composeDocService.saveBatch(tobeSave);
        return Result.ok();
    }

    private static List<ComposeDocAddParam.Doc> filterDocId(List<ComposeDocAddParam.Doc> docList, List<ComposeDoc> composeDocsExist) {
        List<Long> existDocIds = composeDocsExist.stream()
                .map(ComposeDoc::getDocId)
                .collect(Collectors.toList());
        return docList
                .stream().filter(doc -> !existDocIds.contains(doc.getDocId()))
                .collect(Collectors.toList());
    }

    @PostMapping("remove")
    public Result removeDoc(@RequestBody IdParam param) {
        composeDocService.deleteById(param.getId());
        return Result.ok();
    }

    private static ComposeDoc buildComposeDoc(ComposeDocAddParam.Doc doc, ComposeDocAddParam param, User user) {
        ComposeDoc composeDoc = new ComposeDoc();
        LocalDateTime date = LocalDateTime.now();
        Long parentId = param.getParentId();
        if (parentId == null) {
            parentId = 0L;
        }
        composeDoc.setProjectId(param.getProjectId());
        composeDoc.setDocId(doc.getDocId());
        composeDoc.setParentId(parentId);
        composeDoc.setCreator(user.getNickname());
        composeDoc.setIsFolder(Booleans.FALSE);
        composeDoc.setFolderName("");
        composeDoc.setOrigin(doc.getOrigin());
        composeDoc.setIsDeleted(Booleans.FALSE);
        composeDoc.setOrderIndex(doc.getOrderIndex());
        composeDoc.setGmtCreate(date);
        composeDoc.setGmtModified(date);
        return composeDoc;
    }

    @PostMapping("folder/add")
    public Result addFolder(@RequestBody ComposeDocFolderAddParam param) {
        User user = UserContext.getUser();
        ComposeDoc composeDoc = new ComposeDoc();
        composeDoc.setProjectId(param.getProjectId());
        composeDoc.setIsFolder(Booleans.TRUE);
        composeDoc.setFolderName(param.getName());
        composeDoc.setCreator(user.getNickname());
        composeDoc.setParentId(param.getParentId());
        composeDoc.setOrderIndex(param.getOrderIndex());
        composeDocService.save(composeDoc);
        return Result.ok();
    }

    @PostMapping("folder/update")
    public Result updateFolder(@RequestBody ComposeDocFolderUpdateParam param) {
        ComposeDoc composeDoc = composeDocService.getById(param.getId());
        composeDoc.setFolderName(param.getName());
        composeDocService.update(composeDoc);
        return Result.ok();
    }

    @GetMapping("list")
    public Result<List<ComposeDocVO>> list(@HashId Long projectId) {
        List<ComposeDoc> composeDocList = composeDocService.listByProjectId(projectId);
        // 所有文档id
        List<Long> docIdList = composeDocList.stream()
                .filter(composeDoc -> composeDoc.getDocId() > 0)
                .map(ComposeDoc::getDocId)
                .collect(Collectors.toList());
        // 查询出所有文档信息
        List<DocInfo> docInfos = docInfoService.listDocByIds(docIdList);
        Map<Long, DocInfo> docIdMap = docInfos.stream()
                .collect(Collectors.toMap(DocInfo::getId, Function.identity()));

        List<ComposeDocVO> composeDocVOList = composeDocList.stream()
                .map(composeDoc -> {
                    ComposeDocVO composeDocVO = CopyUtil.copyBean(composeDoc, ComposeDocVO::new);
                    composeDocVO.setName(composeDoc.getFolderName());
                    DocInfo docInfo = docIdMap.get(composeDoc.getDocId());
                    if (docInfo != null) {
                        composeDocVO.setHttpMethod(docInfo.getHttpMethod());
                        composeDocVO.setUrl(docInfo.getUrl());
                        composeDocVO.setName(docInfo.getName());
                    }
                    return composeDocVO;
                })
                .collect(Collectors.toList());

        return Result.ok(composeDocVOList);
    }

    @GetMapping("menu")
    @NoLogin
    public Result<List<ComposeDocVO>> menu(@HashId Long projectId) {
        ComposeProject composeProject = composeProjectService.getById(projectId);
        if (composeProject == null || composeProject.getStatus() == StatusEnum.DISABLED.getStatus()) {
            return Result.ok(Collections.emptyList());
        }
        return this.list(projectId);
    }

    @PostMapping("orderindex/update")
    public Result updateOrderIndex(@RequestBody UpdateOrderIndexParam param) {
        ComposeDoc composeDoc = composeDocService.getById(param.getId());
        composeDoc.setOrderIndex(param.getOrderIndex());
        composeDocService.update(composeDoc);
        return Result.ok();
    }

}
