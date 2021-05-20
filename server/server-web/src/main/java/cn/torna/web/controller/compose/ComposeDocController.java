package cn.torna.web.controller.compose;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.ComposeDoc;
import cn.torna.dao.entity.DocInfo;
import cn.torna.service.ComposeDocService;
import cn.torna.service.DocInfoService;
import cn.torna.web.controller.compose.param.ComposeDocAddParam;
import cn.torna.web.controller.compose.param.ComposeDocFolderAddParam;
import cn.torna.web.controller.compose.param.ComposeDocFolderUpdateParam;
import cn.torna.web.controller.compose.vo.ComposeDocVO;
import cn.torna.web.controller.doc.vo.DocInfoVO;
import cn.torna.web.controller.system.param.IdParam;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
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
    private DocInfoService docInfoService;

    @PostMapping("add")
    public Result addDoc(@RequestBody ComposeDocAddParam param) {
        User user = UserContext.getUser();
        List<Long> docIdList = param.getDocIdList();
        if (CollectionUtils.isEmpty(docIdList)) {
            throw new BizException("请选择文档");
        }
        List<ComposeDoc> tobeSave = docIdList.stream()
                .map(docId -> buildComposeDoc(docId, param, user))
                .collect(Collectors.toList());
        composeDocService.saveBatch(tobeSave);
        return Result.ok();
    }

    @PostMapping("remove")
    public Result removeDoc(@RequestBody IdParam param) {
        composeDocService.deleteById(param.getId());
        return Result.ok();
    }

    private static ComposeDoc buildComposeDoc(long docId, ComposeDocAddParam param, User user) {
        ComposeDoc composeDoc = new ComposeDoc();
        Date date = new Date();
        composeDoc.setProjectId(param.getProjectId());
        composeDoc.setDocId(docId);
        composeDoc.setParentId(param.getParentId());
        composeDoc.setCreator(user.getNickname());
        composeDoc.setIsFolder(Booleans.FALSE);
        composeDoc.setFolderName("");
        composeDoc.setIsDeleted(Booleans.FALSE);
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
        List<ComposeDoc> composeDocList = composeDocService.list("project_id", projectId);
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

}
