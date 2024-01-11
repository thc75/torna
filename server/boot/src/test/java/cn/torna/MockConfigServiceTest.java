package cn.torna;

import cn.torna.service.MockConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author thc
 */
public class MockConfigServiceTest extends TornaApplicationTests {

    @Autowired
    MockConfigService mockConfigService;

    @Test
    public void add() throws InterruptedException {
        mockConfigService.createDocDefaultMock(4995L);
        Thread.sleep(2000);
    }

}
