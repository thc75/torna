package cn.torna.common;

import cn.torna.common.util.IdUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author tanghc
 */
public class HashidTest {

    @Test
    public void testGen() {
        Long val = 711111231231235L;
        String hash = IdUtil.encode(val);
        System.out.println(hash);

        Long val2 = IdUtil.decode(hash);
        Assertions.assertEquals(val, val2);
    }

    @Test
    public void testGen2() {
        Long val = 8888L;
        String hash = IdUtil.encode(val);
        System.out.println(hash);

        Long val2 = IdUtil.decode(hash);
        Assertions.assertEquals(val, val2);
    }

}
