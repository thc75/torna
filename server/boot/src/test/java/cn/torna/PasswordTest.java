package cn.torna;

import cn.torna.service.UserInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

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
        String username = "admin";
        String password = DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8));
        String dbPassword = userInfoService.getDbPassword(username, password);
        System.out.println(String.format(ADMIN_INSERT, username, dbPassword));
    }

    /**
     * 批量创建用户
     */
    @Test
    public void testGenBatch() {
        List<String> usernameList = Arrays.asList("jim", "tom");
        String tpl = "INSERT INTO `user_info` ( `username`, `password`, `nickname`, `is_super_admin`) VALUES \n" +
                "\t('%s','%s','%s',0);";
        // 初始密码
        String defPassword = "123456";
        for (String username : usernameList) {
            String password = DigestUtils.md5DigestAsHex(defPassword.getBytes(StandardCharsets.UTF_8));
            String dbPassword = userInfoService.getDbPassword(username, password);
            System.out.println(String.format(tpl, username, dbPassword, username));
        }
    }

    /**
     * 生成数据库中保存的密码
     */
    @Test
    public void testPasswordInDb() {
        // 登录账号
        String username = "admin";
        // 密码，明文
        String password = "123456";
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        String dbPassword = userInfoService.getDbPassword(username, password);
        System.out.println(dbPassword);
    }


}
