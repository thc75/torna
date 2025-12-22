package cn.torna;

import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.dao.entity.SystemConfig;
import cn.torna.service.SystemConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author thc
 */
public class ConfigsTest extends TornaApplicationTests {

    @Autowired
    private SystemConfigService systemConfigService;

    @Test
    public void add() {
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setConfigKey("aaaa");
        systemConfig.setConfigValue("bbbb");
        systemConfig.setRemark("");

        systemConfigService.save(systemConfig);
    }

    @Test
    public void test() {
        String value = EnvironmentKeys.TORNA_PUSH_PRINT_CONTENT.getValue();
        System.out.printf(value);
    }
}
