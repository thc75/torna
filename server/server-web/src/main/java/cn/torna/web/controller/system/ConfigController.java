package cn.torna.web.controller.system;

import cn.torna.common.bean.Result;
import cn.torna.web.controller.system.vo.ConfigVO;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("system")
public class ConfigController {

    @Autowired
    private Environment environment;

    private ConfigVO configVO = new ConfigVO();


    @GetMapping("/config")
    public Result<ConfigVO> get() {
        return Result.ok(configVO);
    }

    @PostConstruct
    public void after() {
        configVO.setEnableCaptcha(BooleanUtils.toBoolean(environment.getProperty("torna.captcha.enable", "false")));
    }
}
