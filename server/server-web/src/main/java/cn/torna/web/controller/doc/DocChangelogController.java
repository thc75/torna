package cn.torna.web.controller.doc;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.dao.entity.DocDiffRecord;
import cn.torna.service.DocDiffRecordService;
import cn.torna.service.dto.DocDiffRecordDTO;
import cn.torna.web.config.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 文档变更历史
 * @author thc
 */
@RestController
@RequestMapping("doc/changelog")
public class DocChangelogController {

    @Autowired
    private DocDiffRecordService docDiffRecordService;

    @GetMapping("list")
    public Result<List<DocDiffRecordDTO>> list(@HashId Long docId) {
        List<DocDiffRecordDTO> docDiffRecordDTOS = docDiffRecordService.listDocDiff(docId);
        return Result.ok(docDiffRecordDTOS);
    }

    @GetMapping("restore")
    public Result restore(@HashId Long id) {
        User user = UserContext.getUser();
        docDiffRecordService.restore(id, user);
        return Result.ok();
    }

}
