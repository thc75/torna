package torna;

import torna.common.bean.LoginUser;
import torna.common.bean.TokenManager;
import torna.common.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.PostConstruct;
import java.util.Date;

@SpringBootTest
class TornaApplicationTests {

    @Autowired
    private TokenManager tokenManager;

    @PostConstruct
    public void after() {
        String test_token = "test-token";
        UserContext.setTokenGetter(() -> test_token);
        LoginUser loginUser = new LoginUser();
        loginUser.setId(1L);
        loginUser.setUsername("test@test.com");
        loginUser.setRealname("张三");
        loginUser.setGmtCreate(new Date());
        tokenManager.setUser(test_token, loginUser);
    }
}
