package cn.torna.web.controller.doc;

import cn.torna.common.bean.Result;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.DocSnapshot;
import cn.torna.service.DocSnapshotService;
import cn.torna.web.controller.doc.vo.DocSnapshotVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.torna.common.annotation.HashId;

import java.util.List;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("doc/snapshot")
public class DocSnapshotController {

    @Autowired
    private DocSnapshotService docSnapshotService;

    @GetMapping("list")
    public Result<List<DocSnapshotVO>> list(@HashId Long docId) {
        List<DocSnapshot> docSnapshots = docSnapshotService.listDocSnapshotBaseInfo(docId);
        List<DocSnapshotVO> docSnapshotVOS = CopyUtil.copyList(docSnapshots, DocSnapshotVO::new);
        return Result.ok(docSnapshotVOS);
    }

    @GetMapping("get")
    public Result<DocSnapshotVO> get(@HashId Long id) {
        DocSnapshot docSnapshot = docSnapshotService.getById(id);
        DocSnapshotVO docSnapshotVO = CopyUtil.copyBean(docSnapshot, DocSnapshotVO::new);
        return Result.ok(docSnapshotVO);
    }

}
