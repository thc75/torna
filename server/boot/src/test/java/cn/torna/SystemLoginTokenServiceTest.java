package cn.torna;

import cn.torna.service.SystemLoginTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author tanghc
 */
public class SystemLoginTokenServiceTest extends TornaApplicationTests {

    @Autowired
    SystemLoginTokenService systemLoginTokenService;

    @Test
    public void del() {
        systemLoginTokenService.del();
    }
}
