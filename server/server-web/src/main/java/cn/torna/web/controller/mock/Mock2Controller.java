package cn.torna.web.controller.mock;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.ResetRequestWrapper;
import cn.torna.common.enums.MockRequestDataTypeEnum;
import cn.torna.common.enums.MockResultTypeEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.RequestUtil;
import cn.torna.common.util.ResponseUtil;
import cn.torna.dao.entity.MockConfig;
import cn.torna.service.MockConfigService;
import cn.torna.service.dto.NameValueDTO;
import cn.torna.web.controller.doc.vo.NameValueVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author tanghc
 */
@Slf4j
@Controller
public class Mock2Controller {

    public static final int ONE_MINUTE_MILLS = 60000;

    @Autowired
    private MockConfigService mockConfigService;

    @RequestMapping("/mock-{moduleId}/**")
    public void mock(
            @PathVariable String moduleId,
            HttpServletRequest req,
            HttpServletResponse response) {
        try {
            ResetRequestWrapper request = new ResetRequestWrapper(req);
            MockConfig mockConfig = findMockConfig(request);
            if (mockConfig == null) {
                response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
                throw new BizException("Mock not configured");
            }
            this.delay(mockConfig);
            this.setResponseHeaders(response, mockConfig);
            response.setStatus(mockConfig.getHttpStatus());
            String responseBody = getResponseBody(mockConfig);
            ResponseUtil.write(response, responseBody);
        } catch (Exception e) {
            if (!(e instanceof BizException)) {
                log.error("mock2 error, path={}", req.getServletPath(), e);
            }
            ResponseUtil.writeText(response, e.getMessage());
        }
    }

    private String getPath(HttpServletRequest request) {
        return request.getServletPath();
    }

    private MockConfig findMockConfig(HttpServletRequest request) {
        String path = getPath(request);
        List<MockConfig> mockConfigs = mockConfigService.listAdvancedMockByPath(request.getMethod(), path);
        if (CollectionUtils.isEmpty(mockConfigs)) {
            return null;
        }
        String ip = RequestUtil.getIP(request);

        for (MockConfig mockConfig : mockConfigs) {
            List<Boolean> result = Arrays.asList(
                    matchIp(ip, mockConfig),
                    matchHeader(request, mockConfig),
                    matchQuery(request, mockConfig),
                    matchRequestData(request, mockConfig)
            );
            boolean bool = true;
            for (Boolean b : result) {
                if (!b) {
                    bool = false;
                    break;
                }
            }
            if (bool) {
                return mockConfig;
            }
        }
        return null;
    }

    private boolean matchRequestData(HttpServletRequest request, MockConfig mockConfig) {
        // 查询字段，query+form
        Map<String, String> paramMap = new LinkedHashMap<>();
        String contentType = request.getContentType();
        if (contentType == null) {
            contentType = "";
        }
        if (contentType.contains("form")) {
            Map<String, String> formFields = RequestUtil.getFormFields(request);
            paramMap.putAll(formFields);
        }

        String body = null;
        if (contentType.contains("json")) {
            body = RequestUtil.getBodyText(request);
        }
        String requestData = mockConfig.getRequestData();
        if (MockRequestDataTypeEnum.of(mockConfig.getRequestDataType()) == MockRequestDataTypeEnum.KV) {
            List<NameValueVO> params = JSON.parseArray(requestData, NameValueVO.class);
            return match(params, paramMap);
        } else {
            if (ObjectUtils.isEmpty(body) && ObjectUtils.isEmpty(requestData)) {
                return true;
            }

            if (body != null && JSONValidator.from(body).getType() == JSONValidator.Type.Object && !ObjectUtils.isEmpty(requestData)) {
                JSONObject reqMap = JSON.parseObject(body);
                JSONObject configMap = JSON.parseObject(requestData);
                return match(configMap, reqMap);
            }
        }
        return false;
    }

    private boolean matchQuery(HttpServletRequest request, MockConfig mockConfig) {
        String queryData = mockConfig.getQueryData();
        if (ObjectUtils.isEmpty(queryData) || "[]".equals(queryData)) {
            return true;
        }
        List<NameValueVO> nameValueList = JSON.parseArray(queryData, NameValueVO.class);
        Map<String, String> paramMap = RequestUtil.parseQueryString(request.getQueryString());
        return match(nameValueList, paramMap);
    }

    private boolean matchHeader(HttpServletRequest request, MockConfig mockConfig) {
        String headerData = mockConfig.getHeaderData();
        if (ObjectUtils.isEmpty(headerData) || "[]".equals(headerData)) {
            return true;
        }
        Map<String, String> headerMap = RequestUtil.getHeader(request);
        List<NameValueVO> nameValueList = JSON.parseArray(headerData, NameValueVO.class);
        return match(nameValueList, headerMap);
    }

    private boolean matchIp(String requestIp, MockConfig mockConfig) {
        Byte isFilterIp = mockConfig.getIsFilterIp();
        if (isFilterIp == null || isFilterIp == Booleans.FALSE) {
            return true;
        }
        String configIp = mockConfig.getIp();
        if (ObjectUtils.isEmpty(configIp)) {
            return true;
        }
        String[] ipList = configIp.split(",");
        for (String ip : ipList) {
            if (Objects.equals(requestIp, ip)) {
                return true;
            }
        }
        return false;
    }

    private boolean match(Map<String, ?> configMap, Map<String, ?> requestMap) {
        for (Map.Entry<String, ?> configEntry : configMap.entrySet()) {
            String key = configEntry.getKey();
            Object expect = configEntry.getValue();

            Object value = requestMap.get(key);

            if (!Objects.equals(String.valueOf(expect), String.valueOf(value))) {
                return false;
            }
        }
        return true;
    }

    private boolean match(List<NameValueVO> params, Map<String, String> paramMap) {
        for (NameValueVO param : params) {
            String value = paramMap.get(param.getName());
            if (!Objects.equals(value, param.getValue())) {
                return false;
            }
        }
        return true;
    }

    private String getResponseBody(MockConfig mockConfig) {
        Byte resultType = mockConfig.getResultType();
        switch (MockResultTypeEnum.of(resultType)) {
            case CUSTOM:
                return mockConfig.getResponseBody();
            case SCRIPT:
                return mockConfig.getMockResult();
            default:
                return "";
        }
    }

    private void delay(MockConfig mockConfig) {
        Integer delayMills = mockConfig.getDelayMills();
        // 延迟返回
        if (delayMills > 0) {
            // 最多允许等待1分钟
            if (delayMills > ONE_MINUTE_MILLS) {
                delayMills = ONE_MINUTE_MILLS;
            }
            try {
                Thread.sleep(delayMills);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setResponseHeaders(HttpServletResponse response, MockConfig mockConfig) {
        String headers = mockConfig.getResponseHeaders();
        List<NameValueDTO> nameValueDTOList = JSON.parseArray(headers, NameValueDTO.class);
        for (NameValueDTO nameValueDTO : nameValueDTOList) {
            response.setHeader(nameValueDTO.getName(), nameValueDTO.getValue());
        }
    }

}
