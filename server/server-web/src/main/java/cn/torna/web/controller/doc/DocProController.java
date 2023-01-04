package cn.torna.web.controller.doc;

import cn.torna.common.bean.Result;
import cn.torna.dao.entity.DocInfo;
import cn.torna.service.DocInfoService;
import cn.torna.web.controller.doc.param.UpdateStatusParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("doc/pro")
public class DocProController {

    @Autowired
    private DocInfoService docInfoService;

    @PostMapping("status/update")
    public Result updateStatus(@RequestBody UpdateStatusParam param) {
        DocInfo docInfo = docInfoService.getById(param.getId());
        docInfo.setStatus(param.getStatus());
        docInfoService.update(docInfo);
        return Result.ok();
    }

}