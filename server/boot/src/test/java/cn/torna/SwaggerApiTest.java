package cn.torna;

import cn.torna.api.bean.ApiUser;
import cn.torna.api.open.SwaggerApi;
import cn.torna.api.open.param.DocPushParam;
import cn.torna.common.bean.User;
import cn.torna.dao.entity.Module;
import cn.torna.service.dto.ImportSwaggerV2DTO;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author thc
 */
public class SwaggerApiTest extends TornaApplicationTests {

    @Autowired
    private SwaggerApi swaggerApi;


    @Test
    public void buildDocPushParam() throws IOException {
        String content = FileUtils.readFileToString(new File("D:\\downloads\\swagger.json"), StandardCharsets.UTF_8);
        OpenAPI openAPI = SwaggerApi.getOpenAPI(content);
        DocPushParam docPushParam = SwaggerApi.buildDocPushParam("tanghc", openAPI);
        System.out.println(JSON.toJSONString(docPushParam));
    }

    @Test
    public void importSwagger() throws IOException, InterruptedException {
        String content = FileUtils.readFileToString(new File("D:\\downloads\\swagger.json"), StandardCharsets.UTF_8);
        User user = new ApiUser();
        ImportSwaggerV2DTO importSwaggerV2DTO = ImportSwaggerV2DTO.builder()
                .projectId(4L)
                .content(content)
                .user(user)
                .ip("127.0.0.1")
                .build();

        Module module = swaggerApi.importSwagger(importSwaggerV2DTO);
        System.out.println(module);
        Thread.sleep(3000);
    }

}
