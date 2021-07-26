package cn.torna.web.controller.mock;

import cn.torna.common.enums.MockResultTypeEnum;
import cn.torna.common.util.IdUtil;
import cn.torna.common.util.ResponseUtil;
import cn.torna.dao.entity.MockConfig;
import cn.torna.service.MockConfigService;
import cn.torna.service.dto.NameValueDTO;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author tanghc
 */
@Controller
@RequestMapping("mock")
public class MockController {

    public static final int ONE_MINUTE_MILLS = 60000;

    private static final String PREFIX = "/mock/";

    @Autowired
    private MockConfigService mockConfigService;

    @Value("${torna.mock.ignore-param:false}")
    private boolean ignoreParam;

    @RequestMapping("/**")
    public void mock(
            HttpServletRequest request,
            HttpServletResponse response) {
        MockConfig mockConfig;
        String mockId = getMockId(request);
        String dataId = buildDataId(request);
        mockConfig = mockConfigService.getByDataId(dataId);
        if (mockConfig == null) {
            Long id = IdUtil.decode(mockId);
            mockConfig = mockConfigService.getById(id);
        }
        if (mockConfig == null) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            ResponseUtil.writeText(response, "mock未配置");
            return;
        }
        this.delay(mockConfig);
        this.setResponseHeaders(response, mockConfig);
        response.setStatus(mockConfig.getHttpStatus());
        String responseBody = getResponseBody(mockConfig);
        ResponseUtil.write(response, responseBody);
    }

    private String getPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        return servletPath.substring(PREFIX.length());
    }

    private String getMockId(HttpServletRequest request) {
        String queryString = request.getQueryString();
        String servletPath = request.getServletPath();
        String mockId = servletPath.substring(PREFIX.length());
        if (StringUtils.hasText(queryString)) {
            mockId = mockId + "?" + queryString;
        }
        return mockId;
    }

    private String buildDataId(HttpServletRequest request) {
        String path = getPath(request);
        if (ignoreParam) {
            return MockConfigService.buildDataId(path);
        }
        String queryString = request.getQueryString();
        queryString = StringUtils.trimLeadingCharacter(queryString, '?');
        if (StringUtils.hasText(queryString)) {
            path = path + '?' + queryString;
        }
        return MockConfigService.buildDataId(path);
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
