package cn.torna.common.util;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.springframework.util.StringUtils;

import java.util.Collections;

public class Markdown2HtmlUtil {

    private static final String RICH_EDITOR_EMPTY_CONTENT = "<p><br></p>";

     /**
      * markdown to html
      * @param content markdown contents
      * @return parse html contents
      */
     public static String markdown2Html(String content) {
         if (!StringUtils.hasText(content)) {
             return RICH_EDITOR_EMPTY_CONTENT;
         }
         MutableDataSet options = new MutableDataSet();
         options.setFrom(ParserEmulationProfile.MARKDOWN);
         //enable table parse!
         options.set(Parser.EXTENSIONS, Collections.singletonList(TablesExtension.create()));
         Parser parser = Parser.builder(options).build();
         HtmlRenderer renderer = HtmlRenderer.builder(options).build();
         Node document = parser.parse(content);
         return renderer.render(document);
     }
}