package cn.torna.common;

import cn.torna.common.util.Markdown2HtmlUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author thc
 */
public class Markdown2HtmlUtilTest {

    @Test
    public void test() {
        List<String> strings = Arrays.asList(
                "# 标题",
                "## 标题2",
                "- aaaa",
                "- bbbb",
                "- cccc",
                "[百度](http://baidu.com)",
               ">这是引用的内容",
                ">>这是引用的内容",
                ">>>>>>>>>>这是引用的内容",
                "```xml\n" +
                        "<dependency>\n" +
                        "    <groupId>net.oschina.durcframework</groupId>\n" +
                        "    <artifactId>fastmybatis-core</artifactId>\n" +
                        "    <version>最新版本</version>\n" +
                        "</dependency>\n" +
                        "```"

        );
        String markdown = String.join("\n", strings);
        System.out.println("markdown：");
        System.out.println(markdown);
        System.out.println("转化后：");
        String html = Markdown2HtmlUtil.markdown2Html(markdown);
        System.out.println(html);
    }

    @Test
    public void test2() {
        List<String> strings = Arrays.asList(
                "<h1>标题</h1>",
                "<h2>标题2</h2>"
        );
        String markdown = String.join("\n", strings);
        String html = Markdown2HtmlUtil.markdown2Html(markdown);
        System.out.println(html);
    }

}
