package cn.torna.common;

import cn.torna.common.util.HtmlTableBuilder;
import cn.torna.common.util.MarkdownTableBuilder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author thc
 */
public class HtmlTableBuilderTest {

    @Test
    public void test() {
        HtmlTableBuilder builder = new HtmlTableBuilder()
                .heads("错误码", "错误描述", "解决方案");
        for (int i = 0; i <10; i++) {
            builder.addRow(Arrays.asList("1000", "未登录", "请登录"));
        }
        System.out.println(builder);
    }

}
