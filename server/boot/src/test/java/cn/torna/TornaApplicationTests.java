package cn.torna;

import cn.torna.common.context.UserContext;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TornaApplication.class)
class TornaApplicationTests {


    public TornaApplicationTests() {
        String test_token = "7YX0lXxR:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjEiLCJleHAiOjE2Mzg5NjA3NzYsImlhdCI6MTYwNzQyNDc3Nn0.Lctn_3q3cNSgIjUBlImQByqUTtjH_zI-OnUeC2dNrYU";
        UserContext.setTokenGetter(() -> test_token);
    }
}
