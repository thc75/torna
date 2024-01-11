package cn.torna.service.metersphere;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.Configs;
import cn.torna.common.bean.HttpHelper;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.MsModuleConfig;
import cn.torna.dao.entity.MsSpaceConfig;
import cn.torna.dao.entity.Project;
import cn.torna.manager.doc.postman.Postman;
import cn.torna.service.ConvertService;
import cn.torna.service.ModuleService;
import cn.torna.service.ProjectService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 负责将文档推送到metersphere
 * @author thc
 */
@Service
@Slf4j
public class MeterSpherePushService {

    @Autowired
    private ConvertService convertService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MsSpaceConfigService msSpaceConfigService;

    @Autowired
    private MsModuleConfigService moduleConfigService;


    /**
     * 推送模块文档到metersphere服务器
     * @param moduleId 模块id
     */
    @Async
    public void push(Long moduleId) throws Exception {
        Module module = moduleService.getById(moduleId);
        Project project = projectService.getById(module.getProjectId());
        MsModuleConfig msModuleConfig = moduleConfigService.getByModuleId(moduleId);
        if (msModuleConfig == null) {
            return;
        }
        MsSpaceConfig msSpaceConfig = msSpaceConfigService.getBySpaceId(project.getSpaceId());
        if (msSpaceConfig == null) {
            log.warn("metersphere空间未配置，spaceId={}, project={}", project.getSpaceId(), project.getName());
            return;
        }

        Postman postman = convertService.convertToPostman(moduleId);
        String url = msSpaceConfig.getMsAddress() + Configs.getValue("metersphere.import-url", "/api/definition/import");

        File file = buildFileContent(postman);
        JSONObject param = buildParam(msModuleConfig);
        HttpEntity formEntity = MultipartEntityBuilder.create()
                .addBinaryBody("file", file, ContentType.APPLICATION_JSON, null)
                .addBinaryBody("request", param.toJSONString().getBytes(StandardCharsets.UTF_8), ContentType.APPLICATION_JSON, null).build();

        try {
            log.debug("推送metersphere，param={}", param);
            HttpHelper.ResponseResult responseResult = HttpHelper.create()
                    .url(url)
                    .method(HttpMethod.POST.name())
                    .header("Accept", "application/json, text/plain, */*")
                    .header("accesskey", msSpaceConfig.getMsAccessKey())
                    .header("signature", MSApiUtil.getSinature(msSpaceConfig.getMsAccessKey(), msSpaceConfig.getMsSecretKey()))
                    .entity(formEntity)
                    .execute();
            String result = responseResult.asString();
            if (responseResult.getStatus() == 200) {
                log.info("推送metersphere成功");
            } else {
                log.error("推送metersphere失败，result={}", result);
            }
        } catch (IOException e) {
            log.error("推送metersphere失败", e);
        }
    }

    private File buildFileContent(Postman postman) throws IOException {
        File temp = File.createTempFile(UUID.randomUUID().toString(), null);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(temp));
        String postmanJson = JSON.toJSONString(postman);
        //log.debug("postman json:{}", postmanJson);
        bufferedWriter.write(postmanJson);
        bufferedWriter.flush();
        bufferedWriter.close();
        return temp;
    }

    private JSONObject buildParam(MsModuleConfig msModuleConfig) {
        JSONObject param = new JSONObject();
        // incrementalMerge,fullCoverage
        param.put("modeId", "fullCoverage");
        param.put("moduleId", msModuleConfig.getMsModuleId());
        param.put("platform", "Postman");
        param.put("model", "definition");
        param.put("projectId", msModuleConfig.getMsProjectId());
        param.put("coverModule", msModuleConfig.getMsCoverModule() == Booleans.TRUE);
        param.put("protocol", "HTTP");
        // 标记导入来源
        // 此处会影响是否会生成case，idea不会生成case
        param.put("origin", "idea");
        return param;
    }

}
