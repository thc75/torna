package cn.torna;

import cn.torna.service.ConstantInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author thc
 */
public class ErrorCodeInfoServiceTest extends TornaApplicationTests{

    @Autowired
    ConstantInfoService errorCodeInfoService;

    @Test
    public void test() {
        String content = errorCodeInfoService.getDocConstantInfo(82);
        System.out.println(content);
    }

}
