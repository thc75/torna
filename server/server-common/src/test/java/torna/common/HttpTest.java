package torna.common;

import org.junit.Test;
import torna.common.bean.HttpHelper;

import java.io.IOException;

/**
 * @author tanghc
 */
public class HttpTest {


    @Test
    public void testGet2() throws IOException {
        String s = HttpHelper.get("https://blog.csdn.net/phoenix/web/v1/comment/list/101426186")
                .execute()
                .asString();
        System.out.println(s);
    }

}
