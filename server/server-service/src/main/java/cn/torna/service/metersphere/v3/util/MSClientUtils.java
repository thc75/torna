package cn.torna.service.metersphere.v3.util;

import cn.torna.service.metersphere.v3.constants.URLConstants;
import cn.torna.service.metersphere.v3.model.state.AppSettingState;
import cn.torna.service.metersphere.v3.model.state.MSModule;
import cn.torna.service.metersphere.v3.model.state.MSOrganization;
import cn.torna.service.metersphere.v3.model.state.MSProject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class MSClientUtils {

    public static final String ACCESS_KEY = "accessKey";
    public static final String SIGNATURE = "signature";

    /**
     * 测试连接
     */
    public static boolean test(AppSettingState appSettingState) {
        if (StringUtils.isAnyBlank(appSettingState.getMeterSphereAddress(), appSettingState.getAccessKey(), appSettingState.getSecretKey())) {
            return false;
        }

        try (CloseableHttpClient httpClient = HttpClients.custom().build()) {
            String userInfoUrl = String.format("%s%s", appSettingState.getMeterSphereAddress(), URLConstants.USER_INFO);
            HttpGet httpGet = new HttpGet(userInfoUrl);
            setupRequestHeaders(httpGet, appSettingState);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    return true;
                } else {
                    LogUtils.error("test failed! Status code: " + statusCode);
                    return false;
                }
            }
        } catch (IOException e) {
            LogUtils.error("测试连接失败！", e);
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据 AK SK 获取项目列表
     */
    public static List<MSProject> getProjectList(AppSettingState appSettingState, String orgId) {
        try (CloseableHttpClient httpClient = HttpClients.custom().build()) {
            HttpGet httpGet = new HttpGet(appSettingState.getMeterSphereAddress() + URLConstants.GET_PROJECT_LIST + "/" + orgId);
            setupRequestHeaders(httpGet, appSettingState);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                return handleResponseList(response, MSProject.class);
            }
        } catch (IOException e) {
            LogUtils.error("getProjectList failed", e);
            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取组织
     */
    public static List<MSOrganization> getOrganizationList(AppSettingState appSettingState) {
        try (CloseableHttpClient httpClient = HttpConfig.getOneHttpClient(appSettingState.getMeterSphereAddress())) {
            HttpGet httpGet = new HttpGet(appSettingState.getMeterSphereAddress() + URLConstants.GET_ORG_LIST);
            setupRequestHeaders(httpGet, appSettingState);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                return handleResponseList(response, MSOrganization.class);
            }
        } catch (IOException e) {
            LogUtils.error("getOrganizationList failed", e);
            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param projectId 项目ID
     */
    public static List<MSModule> getModuleList(AppSettingState appSettingState, String projectId) {
        try (CloseableHttpClient httpClient = HttpConfig.getOneHttpClient(appSettingState.getMeterSphereAddress())) {
            HttpPost httpPost = new HttpPost(appSettingState.getMeterSphereAddress() + URLConstants.GET_API_MODULE_LIST);
            setupRequestHeaders(httpPost, appSettingState);
            StringEntity stringEntity = new StringEntity("{\"projectId\":\"" + projectId + "\"}", ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                List<MSModule> msModules = handleResponseList(response, MSModule.class);
                for (MSModule msModule : msModules) {
                    if (Objects.equals(msModule.getName(), "Unplanned Api")) {
                        msModule.setName("未规划接口");
                    }
                }
                return msModules;
            }
        } catch (IOException e) {
            LogUtils.error("getModuleList failed", e);
            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置请求的头部信息
     */
    private static void setupRequestHeaders(HttpRequestBase request, AppSettingState appSettingState) throws Exception {
        request.addHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
        request.addHeader("Content-type", ContentType.APPLICATION_JSON.toString());
        request.addHeader(ACCESS_KEY, appSettingState.getAccessKey());
        request.addHeader(SIGNATURE, CodingUtils.getSignature(appSettingState));
    }

    /**
     * 处理返回结果为列表的情况
     */
    private static <T> List<T> handleResponseList(HttpResponse response, Class<T> clazz) throws IOException {
        if (response.getStatusLine().getStatusCode() == 200) {
            ResultHolder resultHolder = JsonUtil.getResult(EntityUtils.toString(response.getEntity()));
            if (resultHolder != null && resultHolder.getData() != null) {
                return JsonUtil.parseArray(resultHolder.getData(), clazz);
            }
        }
        return Collections.emptyList();
    }
}

