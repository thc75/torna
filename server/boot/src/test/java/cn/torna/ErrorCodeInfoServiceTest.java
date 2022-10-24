package cn.torna;

import cn.torna.service.ErrorCodeInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author thc
 */
public class ErrorCodeInfoServiceTest extends TornaApplicationTests{

    @Autowired
    ErrorCodeInfoService errorCodeInfoService;

    @Test
    public void test() {
        String content = errorCodeInfoService.getDocErrorCode(82);
        System.out.println(content);
    }

}
