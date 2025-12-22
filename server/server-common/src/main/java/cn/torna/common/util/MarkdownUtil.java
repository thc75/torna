package cn.torna.common.util;

public class MarkdownUtil {

    // 检查常见的Markdown语法特征
    static String[] markdownPatterns = {
            "^#{1,6}\\s",           // 标题
            "\\*\\*[^*]+\\*\\*",    // 加粗
            "\\_\\_[^*]+\\_\\_",    // 加粗
            "\\*[^*]+\\*",          // 斜体
            "\\_[^*]+\\_",          // 斜体
            "\\[.*\\]\\(.*\\)",     // 链接
            "!\\[.*\\]\\(.*\\)",    // 图片
            "^\\s*[-+*]\\s",        // 无序列表
            "^\\s*\\d+\\.\\s",      // 有序列表
            "`[^`]+`",              // 行内代码
    };

    /**
     * 判断字符串是否可能是Markdown格式
     *
     * @param content 待检测的字符串
     * @return 是否可能是Markdown格式
     */
    public static boolean isMarkdown(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        try {
            long matchCount = java.util.Arrays.stream(markdownPatterns)
                    .map(pattern -> java.util.regex.Pattern.compile(pattern,
                            java.util.regex.Pattern.MULTILINE))
                    .map(pattern -> pattern.matcher(content))
                    .mapToLong(matcher -> {
                        int count = 0;
                        while (matcher.find()) {
                            count++;
                        }
                        return count;
                    })
                    .sum();

            // 如果匹配到多个Markdown特征，则认为是Markdown
            return matchCount > 0;
        } catch (Exception e) {
            return false;
        }
    }

}
