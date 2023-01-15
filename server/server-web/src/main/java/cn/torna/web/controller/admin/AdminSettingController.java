package cn.torna.web.controller.admin;

import cn.torna.common.bean.Configs;
import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.bean.Result;
import cn.torna.common.exception.BizException;
import cn.torna.service.SystemConfigService;
import cn.torna.web.controller.system.param.ConfigUpdateParam;
import cn.torna.web.controller.system.vo.AdminConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author thc
 */
@RestController
@RequestMapping("admin/setting")
public class AdminSettingController {

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping("/config/get")
    public Result<AdminConfigVO> getAdminConfig(String[] keys) {
        AdminConfigVO adminConfigVO = new AdminConfigVO();
        adminConfigVO.addConfig(AdminConfigVO.buildItem(EnvironmentKeys.REGISTER_ENABLE));
        for (String key : keys) {
            String value = Configs.getValue(key, null);
            adminConfigVO.addConfig(AdminConfigVO.buildItem(key, value));
        }
        return Result.ok(adminConfigVO);
    }

    @PostMapping("/config/update")
    public Result configUpdate(@RequestBody @Valid List<ConfigUpdateParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            throw new BizException("config can not empty");
        }
        for (ConfigUpdateParam param : params) {
            systemConfigService.setConfig(param.getKey(), param.getValue(), param.getRemark());
        }
        return Result.ok();
    }
}
