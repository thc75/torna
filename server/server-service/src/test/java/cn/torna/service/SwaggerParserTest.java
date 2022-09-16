package cn.torna.service;

import com.alibaba.fastjson.JSON;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author thc
 */
public class SwaggerParserTest {

    @Test
    public void test3() {
        // parse a swagger description from the petstore and get the result
        SwaggerParseResult result = new OpenAPIParser().readLocation("https://petstore3.swagger.io/api/v3/openapi.json", null, null);

        // the parsed POJO
        OpenAPI openAPI = result.getOpenAPI();

        if (result.getMessages() != null) {
            result.getMessages().forEach(System.err::println); // validation errors and warnings
        }

        if (openAPI != null) {
            String json = JSON.toJSONString(openAPI);
            System.out.println(json);
        }
    }

    @Test
    public void test2() {
        // parse a swagger description from the petstore and get the result
        SwaggerParseResult result = new OpenAPIParser().readLocation("https://generator.swagger.io/api/swagger.json", null, null);

        // the parsed POJO
        OpenAPI openAPI = result.getOpenAPI();

        if (result.getMessages() != null) {
            result.getMessages().forEach(System.err::println); // validation errors and warnings
        }

        if (openAPI != null) {
            String json = JSON.toJSONString(openAPI);
            System.out.println(json);
        }
    }

    @Test
    public void testFile() {
        // parse a swagger description from the petstore and get the result
        SwaggerParseResult result = new OpenAPIParser().readLocation("D:\\downloads\\swagger.json", null, null);

        // the parsed POJO
        OpenAPI openAPI = result.getOpenAPI();

        if (result.getMessages() != null) {
            result.getMessages().forEach(System.err::println); // validation errors and warnings
        }

        if (openAPI != null) {
            String json = JSON.toJSONString(openAPI);
            System.out.println(json);
        }
    }

    @Test
    public void testContent() throws IOException {
        // parse a swagger description from the petstore and get the result
//        String content = FileUtils.readFileToString(new File("D:\\downloads\\swagger.json"), StandardCharsets.UTF_8);
        String content = FileUtils.readFileToString(new File("D:\\downloads\\swagger.yaml"), StandardCharsets.UTF_8);
        SwaggerParseResult result = new OpenAPIParser().readContents(content, null, null);

        // the parsed POJO
        OpenAPI openAPI = result.getOpenAPI();

        if (result.getMessages() != null) {
            result.getMessages().forEach(System.err::println); // validation errors and warnings
        }

        if (openAPI != null) {
            String json = JSON.toJSONString(openAPI);
            System.out.println(json);
        }
    }
}
