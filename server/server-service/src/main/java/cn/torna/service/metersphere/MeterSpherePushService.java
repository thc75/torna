package cn.torna.service.metersphere;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.Configs;
import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.bean.HttpHelper;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.MsModuleConfig;
import cn.torna.dao.entity.MsSpaceConfig;
import cn.torna.dao.entity.Project;
import cn.torna.manager.doc.postman.Postman;
import cn.torna.service.ConvertService;
import cn.torna.service.ModuleService;
import cn.torna.service.ProjectService;
import cn.torna.service.metersphere.v3.constants.URLConstants;
import cn.torna.service.metersphere.v3.model.state.AppSettingState;
import cn.torna.service.metersphere.v3.openapi.OpenApiGenerator;
import cn.torna.service.metersphere.v3.util.CodingUtils;
import cn.torna.service.metersphere.v3.util.HttpConfig;
import cn.torna.service.metersphere.v3.util.MSClientUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 负责将文档推送到metersphere
 *
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
     *
     * @param moduleId 模块id
     */
    @Async
    public void push(Long moduleId) throws Exception {
        if (!EnvironmentKeys.ENABLE_METER_SPHERE.getBoolean()) {
            return;
        }
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

        Integer version = msSpaceConfig.getVersion();
        if (Objects.equals(MeterSphereVersion.V2, version)) {
            pushPostman(moduleId, msSpaceConfig, msModuleConfig);
        } else if (Objects.equals(MeterSphereVersion.V3, version)) {
            pushOpenApi(module, msSpaceConfig, msModuleConfig);
        }
    }

    /**
     * 推送模块文档到metersphere服务器
     *
     * @param module 模块
     */
    public void pushOpenApi(
            Module module,
            MsSpaceConfig msSpaceConfig,
            MsModuleConfig msModuleConfig
    ) throws Exception {
        Long moduleId = module.getId();
        OpenAPI openApi = convertService.convertToOpenAPI(moduleId);
        openApi.getInfo().setTitle(module.getName());
        JsonObject apiJsonObject = new OpenApiGenerator().generate(openApi);
        String fileContent = apiJsonObject.toString();
        File temp = createTempFile(fileContent);
        try {
            uploadToServer(msSpaceConfig, msModuleConfig, temp);
        } catch (Exception e) {
            log.error("推送openAPI文档失败, moduleId={}", moduleId, e);
        } finally {
            try {
                Files.deleteIfExists(temp.toPath());
            } catch (IOException e) {
                log.error("Failed to delete temp file", e);
            }
        }
    }

    private void uploadToServer(MsSpaceConfig msSpaceConfig, MsModuleConfig msModuleConfig, File file) {
        try (CloseableHttpClient httpclient = HttpConfig.getOneHttpClient(msSpaceConfig.getMsAddress())) {
            String url = msSpaceConfig.getMsAddress() + URLConstants.API_IMPORT;
            HttpPost httpPost = new HttpPost(url);

            AppSettingState state = new AppSettingState();
            state.setAccessKey(msSpaceConfig.getMsAccessKey());
            state.setSecretKey(msSpaceConfig.getMsSecretKey());
            httpPost.setHeader("Accept", "application/json, text/plain, */*");
            httpPost.setHeader(MSClientUtils.ACCESS_KEY, msSpaceConfig.getMsAccessKey());
            httpPost.setHeader(MSClientUtils.SIGNATURE, CodingUtils.getSignature(state));

            Map<String, Object> param = buildParamV3(msModuleConfig);
            HttpEntity formEntity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", file, ContentType.APPLICATION_JSON, file.getName())
                    .addBinaryBody("request", JSON.toJSONBytes(param), ContentType.APPLICATION_JSON, null)
                    .build();

            httpPost.setEntity(formEntity);

            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200 || statusCode == 201) {
                    log.info("推送文档到MeterSphere成功, name={}", msModuleConfig.getName());
                } else {
                    log.error("推送metersphere失败，param={}, result={}", param, statusCode);
                }
            } catch (IOException e) {
                log.error("Failed to upload to MeterSphere", e);
                throw new RuntimeException("Failed to upload to MeterSphere", e);
            }
        } catch (Exception e) {
            log.error("Failed to communicate with MeterSphere server", e);
            throw new RuntimeException("Failed to communicate with MeterSphere server", e);
        }
    }

    private void pushPostman(
            Long moduleId,
            MsSpaceConfig msSpaceConfig,
            MsModuleConfig msModuleConfig
    ) throws IOException {
        ConvertService.Config config = new ConvertService.Config();
        config.setNeedHost(false);
        Postman postman = convertService.convertToPostman(moduleId, config);
        String url = msSpaceConfig.getMsAddress() + Configs.getValue("metersphere.import-url", "/api/definition/import");

        File file = createTempFile(JSON.toJSONString(postman));
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
                log.error("推送metersphere失败，param={}, result={}", param, result);
            }
        } catch (IOException e) {
            log.error("推送metersphere失败，param={},", param, e);
        } finally {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                log.error("Failed to delete temp file", e);
            }
        }
    }


    private File createTempFile(String content) throws IOException {
        File temp = File.createTempFile(UUID.randomUUID().toString(), null);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(temp));
        log.debug("content:{}", content);
        bufferedWriter.write(content);
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

    private Map<String, Object> buildParamV3(MsModuleConfig msModuleConfig) {
        Map<String, Object> param = new HashMap<>();
        param.put("coverModule", true);
        param.put("type", "API");
        param.put("platform", "Swagger3");
        param.put("syncCase", true);
        param.put("moduleId", msModuleConfig.getMsModuleId());
        param.put("model", "definition");
        param.put("projectId", msModuleConfig.getMsProjectId());
        param.put("protocol", "HTTP");
        // Marking the import source as 'idea'
        param.put("origin", "idea");
        return param;
    }

}
