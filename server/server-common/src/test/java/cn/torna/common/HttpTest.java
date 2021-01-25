package cn.torna.common;

import cn.torna.common.bean.HttpHelper;
import org.junit.Test;

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
