package cn.torna.common;

import cn.torna.common.util.HtmlUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author tanghc
 */
public class HtmlUtilTest {

    @Test
    public void test() {
        List<String> resultsFromHtml = HtmlUtil.parseHtmlContent(" <h1 class=\"curproject-name\"> 大黄蜂API </h1> ");
        System.out.println(resultsFromHtml);
    }

}
