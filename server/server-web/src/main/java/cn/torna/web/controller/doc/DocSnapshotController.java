package cn.torna.web.controller.doc;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.DocSnapshot;
import cn.torna.service.DocSnapshotService;
import cn.torna.web.controller.doc.vo.DocCompareVO;
import cn.torna.web.controller.doc.vo.DocSnapshotVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 文档快照
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

    @GetMapping("docinfo")
    public Result<JSONObject> getByMd5(String md5) {
        DocSnapshot docSnapshot = docSnapshotService.getByMd5(md5);
        String content = docSnapshot.getContent();
        JSONObject jsonObject = JSON.parseObject(content);
        return Result.ok(jsonObject);
    }

    @GetMapping("compare-info")
    public Result<DocCompareVO> getCompareDoc(String md5Old, String md5New) {
        DocSnapshot docSnapshot = docSnapshotService.getByMd5(md5Old);
        String content = docSnapshot.getContent();
        JSONObject docOld = JSON.parseObject(content);

        docSnapshot = docSnapshotService.getByMd5(md5New);
        content = docSnapshot.getContent();
        JSONObject docNew = JSON.parseObject(content);

        DocCompareVO docCompareVO = new DocCompareVO();
        docCompareVO.setDocOld(docOld);
        docCompareVO.setDocNew(docNew);


        return Result.ok(docCompareVO);
    }

}
