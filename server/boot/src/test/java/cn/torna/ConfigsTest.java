package cn.torna;

import cn.torna.common.bean.Configs;
import org.junit.jupiter.api.Test;

/**
 * @author thc
 */
public class ConfigsTest extends TornaApplicationTests {

    @Test
    public void test() {
        String value = Configs.getValue("torna.push.print-content", "false");
        System.out.printf(value);
    }
}
