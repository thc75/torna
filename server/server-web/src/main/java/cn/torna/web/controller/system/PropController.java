package cn.torna.web.controller.system;

import cn.torna.common.annotation.HashId;
import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.Result;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.Prop;
import cn.torna.service.PropService;
import cn.torna.web.controller.system.param.PropParam;
import cn.torna.web.controller.system.param.PropsListParam;
import cn.torna.web.controller.system.param.PropsParam;
import cn.torna.web.controller.system.vo.PropVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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

    @PostMapping("save")
    public Result save(@RequestBody PropsListParam param) {
        List<PropParam> propList = param.getPropList();
        List<Prop> props = CopyUtil.copyList(propList, Prop::new);
        propService.save(props);
        return Result.ok();
    }

    @GetMapping("get")
    public Result get(@HashId Long refId, byte type) {
        Map<String, String> props = propService.getProps(refId, type);
        return Result.ok(props);
    }

    @GetMapping("find")
    public Result<PropVO> list(@HashId Long refId, byte type, String name) {
        Prop prop = propService.get(refId, type, name);
        if (prop == null) {
            return Result.ok();
        }
        PropVO propVO = CopyUtil.copyBean(prop, PropVO::new);
        return Result.ok(propVO);
    }

}
