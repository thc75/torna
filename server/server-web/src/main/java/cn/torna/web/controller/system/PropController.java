package cn.torna.web.controller.system;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.service.PropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("prop")
public class PropController {

    @Autowired
    private PropService propService;

    @GetMapping("getDocProps")
    public Result getDocProp(@HashId Long id) {
        Map<String, String> docProps = propService.getDocProps(id);
        return Result.ok(docProps);
    }

}
