package cn.torna.web.controller.doc;

import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.service.DocInfoProService;
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
    private DocInfoProService docInfoProService;

    @PostMapping("status/update")
    public Result updateStatus(@RequestBody UpdateStatusParam param) {
        User user = UserContext.getUser();
        docInfoProService.updateStatus(param.getId(), param.getStatus(), user);
        return Result.ok();
    }

}