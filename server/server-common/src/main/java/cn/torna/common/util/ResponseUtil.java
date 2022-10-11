package cn.torna.common.util;

import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseUtil {

    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";
    public static final String UTF_8 = "UTF-8";

    public static void writeText(HttpServletResponse response, String content) {
        response.setCharacterEncoding(UTF_8);
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        write(response, content);
    }

    public static void writeHtml(HttpServletResponse response, String content) {
        response.setCharacterEncoding(UTF_8);
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        write(response, content);
    }

    public static void writeJson(HttpServletResponse response, String content) {
        response.setContentType(APPLICATION_JSON_CHARSET_UTF_8);
        write(response, content);
    }

    public static void write(HttpServletResponse response, String content) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
}