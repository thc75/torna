package cn.torna;

import cn.torna.common.bean.ApiUser;
import cn.torna.api.open.SwaggerApi;
import cn.torna.api.open.param.DocPushParam;
import cn.torna.common.bean.User;
import cn.torna.dao.entity.Module;
import cn.torna.service.dto.ImportSwaggerV2DTO;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author thc
 */
public class SwaggerApiTest extends TornaApplicationTests {

    @Autowired
    private SwaggerApi swaggerApi;


    @Test
    public void buildDocPushParam() throws IOException {
        String content = FileUtils.readFileToString(new File("/Users/thc/Downloads/bbb.txt"), StandardCharsets.UTF_8);
        OpenAPI openAPI = SwaggerApi.getOpenAPI(content);
        DocPushParam docPushParam = SwaggerApi.buildDocPushParam("tanghc", openAPI);
        System.out.println(JSON.toJSONString(docPushParam));
    }

    @Test
    public void importSwagger0() throws IOException, InterruptedException {
        String content = FileUtils.readFileToString(new File("/Users/thc/Downloads/test.json"), StandardCharsets.UTF_8);
        User user = new ApiUser();
        ImportSwaggerV2DTO importSwaggerV2DTO = ImportSwaggerV2DTO.builder()
                .projectId(5L)
                .content(content)
                .user(user)
                .ip("127.0.0.1")
                .build();

        Module module = swaggerApi.importSwagger(importSwaggerV2DTO);
        System.out.println(module);
        Thread.sleep(3000);
    }

    @Test
    public void importSwagger1117() throws IOException, InterruptedException {
        String content = FileUtils.readFileToString(new File("/Users/thc/Downloads/默认模块.openapi.json"), StandardCharsets.UTF_8);
        User user = new ApiUser();
        ImportSwaggerV2DTO importSwaggerV2DTO = ImportSwaggerV2DTO.builder()
                .projectId(5L)
                .content(content)
                .user(user)
                .ip("127.0.0.1")
                .build();

        Module module = swaggerApi.importSwagger(importSwaggerV2DTO);
        System.out.println(module);
        Thread.sleep(3000);
    }

    @Test
    public void importSwagger() throws IOException, InterruptedException {
        ClassPathResource resource = new ClassPathResource("/Users/thc/Downloads/test.json");
        InputStream inputStream = resource.getInputStream();
        String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        User user = new ApiUser();
        ImportSwaggerV2DTO importSwaggerV2DTO = ImportSwaggerV2DTO.builder()
                .projectId(5L)
                .content(content)
                .user(user)
                .ip("127.0.0.1")
                .build();

        Module module = swaggerApi.importSwagger(importSwaggerV2DTO);
        System.out.println(module);
        Thread.sleep(3000);
    }

    @Test
    public void importSwagger2() throws IOException, InterruptedException {
        ClassPathResource resource = new ClassPathResource("petstore-swagger2.json");
        InputStream inputStream = resource.getInputStream();
        String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        User user = new ApiUser();
        ImportSwaggerV2DTO importSwaggerV2DTO = ImportSwaggerV2DTO.builder()
                .projectId(6L)
                .content(content)
                .user(user)
                .ip("127.0.0.1")
                .build();

        Module module = swaggerApi.importSwagger(importSwaggerV2DTO);
        System.out.println(module);
        Thread.sleep(3000);
    }

    @Test
    public void importSwagger22() throws IOException, InterruptedException {
        String content = FileUtils.readFileToString(new File("/Users/thc/Downloads/test.json"), StandardCharsets.UTF_8);
        User user = new ApiUser();
        ImportSwaggerV2DTO importSwaggerV2DTO = ImportSwaggerV2DTO.builder()
                .projectId(6L)
                .content(content)
                .user(user)
                .ip("127.0.0.1")
                .build();

        Module module = swaggerApi.importSwagger(importSwaggerV2DTO);
        System.out.println(module);
        Thread.sleep(3000);
    }

    @Test
    public void importSwagger3() throws IOException, InterruptedException {
        ClassPathResource resource = new ClassPathResource("商品中心11.openapi3.json");
        InputStream inputStream = resource.getInputStream();
        String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        User user = new ApiUser();
        ImportSwaggerV2DTO importSwaggerV2DTO = ImportSwaggerV2DTO.builder()
                .projectId(6L)
                .content(content)
                .user(user)
                .ip("127.0.0.1")
                .build();

        Module module = swaggerApi.importSwagger(importSwaggerV2DTO);
        System.out.println(module);
        Thread.sleep(3000);
    }

    @Test
    public void importSwagger4() throws IOException, InterruptedException {
        ClassPathResource resource = new ClassPathResource("商品中心11.openapi.yaml");
        InputStream inputStream = resource.getInputStream();
        String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        User user = new ApiUser();
        ImportSwaggerV2DTO importSwaggerV2DTO = ImportSwaggerV2DTO.builder()
                .projectId(6L)
                .content(content)
                .user(user)
                .ip("127.0.0.1")
                .build();

        Module module = swaggerApi.importSwagger(importSwaggerV2DTO);
        System.out.println(module);
        Thread.sleep(3000);
    }

    @Test
    public void importSwaggerPet() throws IOException, InterruptedException {
        ClassPathResource resource = new ClassPathResource("petstore.yaml");
        InputStream inputStream = resource.getInputStream();
        String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        User user = new ApiUser();
        ImportSwaggerV2DTO importSwaggerV2DTO = ImportSwaggerV2DTO.builder()
                .projectId(6L)
                .content(content)
                .user(user)
                .ip("127.0.0.1")
                .build();

        Module module = swaggerApi.importSwagger(importSwaggerV2DTO);
        System.out.println(module);
        Thread.sleep(3000);
    }

}
