package cn.torna.web.controller.system;

import cn.torna.common.annotation.HashId;
import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.Result;
import cn.torna.service.PropService;
import cn.torna.web.controller.system.param.PropsParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("prop")
@NoLogin
public class PropController {

    @Autowired
    private PropService propService;

    @GetMapping("getDocProps")
    public Result getDocProp(@HashId Long id) {
        Map<String, String> docProps = propService.getDocProps(id);
        return Result.ok(docProps);
    }


    @PostMapping("set")
    public Result setProps(@RequestBody PropsParam param) {
        propService.saveProps(param.getProps(), param.getRefId(), param.getType());
        return Result.ok();
    }

    @GetMapping("get")
    public Result get(@HashId Long refId, byte type) {
        Map<String, String> props = propService.getProps(refId, type);
        return Result.ok(props);
    }

}
