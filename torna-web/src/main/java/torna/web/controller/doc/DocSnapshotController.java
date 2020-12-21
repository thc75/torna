package torna.web.controller.doc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.annotation.HashId;
import torna.common.bean.Result;
import torna.common.util.CopyUtil;
import torna.dao.entity.DocSnapshot;
import torna.service.DocSnapshotService;
import torna.web.controller.doc.vo.DocSnapshotVO;

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
