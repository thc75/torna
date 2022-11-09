package cn.torna;

import cn.torna.common.bean.EnvironmentKeys;
import org.junit.jupiter.api.Test;

/**
 * @author thc
 */
public class ConfigsTest extends TornaApplicationTests {

    @Test
    public void test() {
        String value = EnvironmentKeys.TORNA_PUSH_PRINT_CONTENT.getValue();
        System.out.printf(value);
    }
}
