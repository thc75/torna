package cn.torna.service.metersphere;

import cn.torna.common.bean.HttpHelper;
import cn.torna.common.util.CodingUtil;
import cn.torna.dao.entity.MsSpaceConfig;
import cn.torna.service.metersphere.dto.MeterSphereSetting;
import com.alibaba.fastjson.JSONObject;
//import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;

@Slf4j
public class MSApiUtil {

    /**
     * 测试连接
     *
     * @return
     */
    public static boolean test(MeterSphereSetting meterSphereSetting) throws Exception {
        String address = meterSphereSetting.getMsAddress();
        String accessKey = meterSphereSetting.getMsAccessKey();
        String secretKey = meterSphereSetting.getMsSecretKey();
        if (StringUtils.isAnyBlank(address, accessKey, secretKey)) {
            throw new IllegalArgumentException("配置项不能为空");
        }
        String url = String.format("%s/currentUser", address);
        HttpHelper.ResponseResult responseResult = HttpHelper.get(url)
                .header("Accept", "application/json;charset=UTF-8")
                .header("accessKey", accessKey)
                .header("signature", getSinature(accessKey, secretKey))
                .execute();

        try {
            if (responseResult.getStatus() == 200) {
                return true;
            }
            log.error("test failed! response:{}", responseResult.asString());
            return false;
        } finally {
            responseResult.closeResponse();
        }
    }

    public static String getSinature(String accessKey, String secretKey) {
        return CodingUtil.aesEncrypt(accessKey + "|" + UUID.randomUUID() + "|" + System.currentTimeMillis(), secretKey, accessKey);
    }

    /**
     * 获取项目列表
     *
     * @param meterSphereSetting
     * @return
     */
    public static JSONObject getProjectList(MeterSphereSetting meterSphereSetting, String workspaceId) throws Exception {
        String url = String.format("%s/project/list/related", meterSphereSetting.getMsAddress());
        JSONObject param = new JSONObject();
        param.put("workspaceId", workspaceId);
        HttpHelper.ResponseResult responseResult = HttpHelper.postJson(url, param.toJSONString())
                .header("accessKey", meterSphereSetting.getMsAccessKey())
                .header("signature", getSinature(meterSphereSetting.getMsAccessKey(), meterSphereSetting.getMsSecretKey()))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .execute();

        String result = responseResult.asString();
        if (responseResult.getStatus() == 200) {
            return JSONObject.parseObject(result);
        }
        log.error("获取MeterSphere项目失败, url={}, result={}", url, result);
        throw new Exception(result);
    }

//    /**
//     * 根据选中的项目id获取项目版本
//     *
//     * @param appSettingState 应用配置状态管理器
//     * @return 如果成功查询到版本返回 response 对象,否则返回 {@code null}
//     */
//    public static JSONObject listProjectVersionBy(String projectId, AppSettingState appSettingState) {
//        if (StringUtils.isAnyBlank(projectId, appSettingState.getMeterSphereAddress())) {
//            return null;
//        }
//        CloseableHttpClient httpClient = HttpFutureUtils.getOneHttpClient();
//        try {
//            String url = !appSettingState.getMeterSphereAddress().endsWith("api") ? String.format("%s/project/version/get-project-versions/%s",
//                    appSettingState.getMeterSphereAddress(), projectId) :
//                    String.format("%s/project/version/get-project-versions/%s",
//                            appSettingState.getMeterSphereAddress().substring(0, appSettingState.getMeterSphereAddress().lastIndexOf("api")) + "project", projectId);
//            HttpGet httPost = new HttpGet(url);
//            httPost.addHeader("accessKey", appSettingState.getAccesskey());
//            httPost.addHeader("signature", getSinature(appSettingState));
//            CloseableHttpResponse response = httpClient.execute(httPost);
//            if (!isSuccessful(response)) {
//                return null;
//            }
//            return JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
//        } catch (Exception e) {
//            log.error("list project versions failed", e);
//            return null;
//        } finally {
//            if (httpClient != null) {
//                try {
//                    httpClient.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private static boolean isSuccessful(CloseableHttpResponse response) {
        return response.getStatusLine() != null
                && response.getStatusLine().getStatusCode() == 200;
    }

    /**
     * @param appSettingState
     * @return
     */
//    public static JSONObject getUserInfo(AppSettingState appSettingState) {
//        CloseableHttpClient httpClient = HttpFutureUtils.getOneHttpClient();
//        try {
//            String url = appSettingState.getMeterSphereAddress() + "/user/key/validate";
//            HttpGet httPost = new HttpGet(url);
//            httPost.addHeader("accessKey", appSettingState.getAccesskey());
//            httPost.addHeader("signature", getSinature(appSettingState));
//            CloseableHttpResponse response = httpClient.execute(httPost);
//            if (response.getStatusLine().getStatusCode() == 200) {
//                return JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
//            }
//            InputStream content = response.getEntity().getContent();
//            String result = IOUtils.toString(content, StandardCharsets.UTF_8);
//
//            log.error("getUserInfo failed! response:【" + result + "】, url={}", url);
//        } catch (Exception e) {
//            log.error("getUserInfo failed", e);
//            return null;
//        } finally {
//            if (httpClient != null) {
//                try {
//                    httpClient.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }

    /**
     * 获取工作空间
     *
     * @param meterSphereSetting
     * @return
     */
    public static JSONObject getWorkSpaceList(MeterSphereSetting meterSphereSetting) throws Exception {
        String url = String.format("%s/workspace/list/userworkspace", meterSphereSetting.getMsAddress());
        HttpHelper.ResponseResult responseResult = HttpHelper.get(url)
                .header("accessKey", meterSphereSetting.getMsAccessKey())
                .header("signature", getSinature(meterSphereSetting.getMsAccessKey(), meterSphereSetting.getMsSecretKey()))
                .execute();

        String result = responseResult.asString();
        if (responseResult.getStatus() == 200) {
            return JSONObject.parseObject(result);
        }
        log.error("获取MeterSphere空间失败, url={}, result={}", url, result);
        throw new RuntimeException(result);
    }

    /**
     * @param meterSphereSetting
     * @param projectId       项目ID
     * @param protocol        协议 1.0.0 暂时只支持 HTTP
     * @return
     */
    public static JSONObject getModuleList(MeterSphereSetting meterSphereSetting, String projectId, String protocol) throws Exception {
        String url = String.format("%s/api/module/list/%s/%s", meterSphereSetting.getMsAddress(), projectId, protocol);
        HttpHelper.ResponseResult responseResult = HttpHelper.get(url)
                .header("accessKey", meterSphereSetting.getMsAccessKey())
                .header("signature", getSinature(meterSphereSetting.getMsAccessKey(), meterSphereSetting.getMsSecretKey()))
                .execute();

        String result = responseResult.asString();
        if (responseResult.getStatus() == 200) {
            return JSONObject.parseObject(result);
        }
        log.error("获取MeterSphere模块失败, url={}, result={}", url, result);
        throw new Exception(result);
    }

//    /**
//     * @param appSettingState
//     * @param projectId       项目ID
//     * @return
//     */
//    public static boolean getProjectVersionEnable(AppSettingState appSettingState, String projectId) {
//        CloseableHttpClient httpClient = HttpFutureUtils.getOneHttpClient();
//        try {
//            HttpGet httpGet = new HttpGet(appSettingState.getMeterSphereAddress() + "/project/version/enable/" + projectId);
//            httpGet.addHeader("accessKey", appSettingState.getAccesskey());
//            httpGet.addHeader("signature", getSinature(appSettingState));
//
//            CloseableHttpResponse response = httpClient.execute(httpGet);
//            if (response.getStatusLine().getStatusCode() == 200) {
//                JSONObject r = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
//                if (r.containsKey("success")) {
//                    if (r.getBoolean("success") && r.getBoolean("data") != null) {
//                        return r.getBoolean("data");
//                    }
//                }
//                return false;
//            }
//        } catch (Exception e) {
//            log.error("getProjectVersionEnable failed", e);
//            return false;
//        } finally {
//            if (httpClient != null) {
//                try {
//                    httpClient.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     * @param appSettingState
//     * @return
//     */
//    public static JSONObject getLicense(AppSettingState appSettingState) {
//        CloseableHttpClient httpClient = HttpFutureUtils.getOneHttpClient();
//        try {
//            HttpGet httpGet = new HttpGet(appSettingState.getMeterSphereAddress() + "/license/valid");
//            httpGet.addHeader("accessKey", appSettingState.getAccesskey());
//            httpGet.addHeader("signature", getSinature(appSettingState));
//            CloseableHttpResponse response = httpClient.execute(httpGet);
//            if (response.getStatusLine().getStatusCode() == 200) {
//                return JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
//            }
//        } catch (Exception e) {
//            log.error("get license failed", e);
//            return null;
//        } finally {
//            if (httpClient != null) {
//                try {
//                    httpClient.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }
//
//    public static String getModeId(String modeId) {
//        if ("COVER".equalsIgnoreCase(modeId)) {
//            return "fullCoverage";
//        }
//        return "incrementalMerge";
//    }
}
