package cn.torna.web.controller.system;

import cn.torna.common.bean.Result;
import cn.torna.web.controller.system.vo.ConfigVO;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("system")
public class ConfigController implements InitializingBean {

    @Autowired
    private Environment environment;

    private ConfigVO configVO = new ConfigVO();


    @GetMapping("/config")
    public Result<ConfigVO> get() {
        return Result.ok(configVO);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        configVO.setEnableReg(BooleanUtils.toBoolean(environment.getProperty("torna.register.enable", "true")));
    }
}
