package cn.torna.common.util;

import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseUtil {

    public static void writeText(HttpServletResponse response, String content) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        write(response, content);
    }

    public static void writeHtml(HttpServletResponse response, String content) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        write(response, content);
    }

    public static void writeJson(HttpServletResponse response, String content) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
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