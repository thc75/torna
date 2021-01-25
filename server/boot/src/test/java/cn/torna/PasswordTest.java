package cn.torna;

import cn.torna.service.UserInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author tanghc
 */
public class PasswordTest extends TornaApplicationTests {

    @Autowired
    private UserInfoService userInfoService;

    String ADMIN_INSERT = "INSERT INTO `user_info` ( `username`, `password`, `nickname`, `is_super_admin`) VALUES \n" +
            "\t('%s','%s','超级管理员',1);";

    /**
     * 生成超级管理员insert语句
     */
    @Test
    public void testGen() {
        String username = "admin@torna.org";
        String password = DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8));
        String dbPassword = userInfoService.getDbPassword(username, password);
        System.out.println(String.format(ADMIN_INSERT, username, dbPassword));
    }

    @Test
    public void testGen2() {
        String username = "zs@qq.com";
        String password = DigestUtils.md5DigestAsHex("123123".getBytes(StandardCharsets.UTF_8));
        String dbPassword = userInfoService.getDbPassword(username, password);
        System.out.println(dbPassword);
    }



}
