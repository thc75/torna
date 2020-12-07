package torna;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import torna.service.UserInfoService;

import java.nio.charset.StandardCharsets;

/**
 * @author tanghc
 */
public class PasswordTest extends TornaApplicationTests {

    @Autowired
    private UserInfoService userInfoService;

    @Test
    public void testGen() {
        String username = "admin@qq.com";
        String password = DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8));
        String dbPassword = userInfoService.getDbPassword(username, password);
        System.out.println(dbPassword);
    }

    @Test
    public void testGen2() {
        String username = "zs@qq.com";
        String password = DigestUtils.md5DigestAsHex("123123".getBytes(StandardCharsets.UTF_8));
        String dbPassword = userInfoService.getDbPassword(username, password);
        System.out.println(dbPassword);
    }



}
