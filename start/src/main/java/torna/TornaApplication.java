package torna;

import torna.common.bean.LoginUser;
import torna.common.bean.TokenManager;
import torna.common.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.Date;

@SpringBootApplication
public class TornaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TornaApplication.class, args);
    }

    @Autowired
    private TokenManager tokenManager;

    static String test_token = "test-token";
    static {
        UserContext.setTokenGetter(() -> test_token);
    }

    @PostConstruct
    public void after() {
        LoginUser loginUser = new LoginUser();
        loginUser.setId(1L);
        loginUser.setUsername("admin@qq.com");
        loginUser.setRealname("超级管理员");
        loginUser.setGmtCreate(new Date());
        loginUser.setIsAdmin((byte)1);
        tokenManager.setUser(test_token, loginUser);
    }

}
