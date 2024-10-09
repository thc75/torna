package cn.torna.web.controller.doc;

import cn.torna.common.annotation.HashId;
import cn.torna.manager.doc.postman.Postman;
import cn.torna.service.ConvertService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author thc
 */
@Controller
@RequestMapping("doc/export")
public class DocExportController {

    @Autowired
    private ConvertService convertService;

    /**
     * 导出postman文件
     *
     * @param moduleId 应用id
     * @throws IOException 异常
     */
    @GetMapping("/postman")
    public void exportPostman(@HashId Long moduleId, HttpServletResponse response) throws IOException {
        ConvertService.Config config = new ConvertService.Config();
        config.setNeedHost(true);
        config.setFormatJson(true);
        Postman postman = convertService.convertToPostman(moduleId, config);
        String json = JSON.toJSONString(postman, SerializerFeature.PrettyFormat);

        HttpHeaders headers = new HttpHeaders();
        String name = postman.getInfo().getName();

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", UriUtils.encode(name, StandardCharsets.UTF_8) + "-postman.json");

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            response.setHeader(entry.getKey(), String.join(";", entry.getValue()));
        }

        IOUtils.copy(new ByteArrayInputStream(json.getBytes()), response.getOutputStream());
    }

}
