package cn.torna.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tanghc
 */
public class HtmlUtil {

    /**
     * 匹配html标签，例如"<p>xxx</p>"这种格式
     */
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[a-zA-Z\\d*]*+.*?>([\\s\\S]*?)</[a-zA-Z\\d*]*?>");

    private static final String[] ATTRS = {"style", "class", "name", "title", "alt", "index", "width"};

    /**
     * 获取html中的数据
     * @param htmlString {@literal  <h1 class="curproject-name"> xxxAPI </h1> }
     * @return 返回：xxxAPI
     */
    public static List<String> parseHtmlContent(String htmlString) {
        htmlString = htmlString.trim();
        List<String> results = new ArrayList<>();
        // 数据预处理
        htmlString = removeBrTag(htmlString);
        // 移除属性
        for (String attr : ATTRS) {
            htmlString = replaceAttr(htmlString, attr);
        }
        if (htmlString != null && htmlString.length() > 0) {
            Matcher imageTagMatcher = HTML_TAG_PATTERN.matcher(htmlString);
            // 针对多个并列的标签的情况
            while (imageTagMatcher.find()) {
                String result = "";
                // group(1)对应正则表达式中的圆括号括起来的数据
                result = imageTagMatcher.group(1).trim();

                // 针对多个标签嵌套的情况进行处理
                if (result != null && result.length() > 0) {
                    result = replaceStartTag(result);
                }

                results.add(result.trim());
            }
        }
        return results;
    }

    /**
     * 针对多个标签嵌套的情况进行处理
     * 比如 <p><span style="white-space: normal;">王者荣耀</span></p>
     * 预处理并且正则匹配完之后结果是 <span>王者荣耀
     * 需要手工移除掉前面的起始标签
     * @param content
     * @return
     */
    public static String replaceStartTag(String content) {
        if (content == null || content.length() == 0) {
            return content;
        }
        String regEx = "<[a-zA-Z]*?>([\\s\\S]*?)";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(content);
        if (m.find()) {
            content = m.replaceAll("");
        }
        return content;
    }

    /**
     * 移除掉</br>标签
     *
     * @param src
     * @return
     */
    public static String removeBrTag(String src) {
        if (src != null && !src.isEmpty()) {
            src = src.replaceAll("<br/>", "");
        }
        return src;
    }

    /**
     * 替换掉html标签里面的属性内容
     *
     * @param content
     * @param attr style，class
     * @return
     */
    public static String replaceAttr(String content, String attr) {
        if (content == null || content.length() == 0) {
            return content;
        }
        String regEx = " " + attr + "=\"(.*?)\"";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(content);
        if (m.find()) {
            content = m.replaceAll("");
        }
        return content;
    }



}
