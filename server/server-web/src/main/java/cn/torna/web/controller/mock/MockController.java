package cn.torna.web.controller.mock;

import cn.torna.common.enums.MockResultTypeEnum;
import cn.torna.common.util.IdUtil;
import cn.torna.common.util.RequestUtil;
import cn.torna.common.util.ResponseUtil;
import cn.torna.dao.entity.MockConfig;
import cn.torna.service.MockConfigService;
import cn.torna.service.dto.NameValueDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tanghc
 */
@Controller
@RequestMapping("mock")
@Slf4j
public class MockController {

    public static final int ONE_MINUTE_MILLS = 60000;

    private static final String PREFIX = "/mock/";

    @Autowired
    private MockConfigService mockConfigService;

    @RequestMapping("/**")
    public void mock(
            HttpServletRequest request,
            HttpServletResponse response) {
        MockConfig mockConfig;
        String mockId = getMockId(request);
        String dataId = buildDataId(mockId, request);
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

    private String getMockId(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        return servletPath.substring(PREFIX.length());
    }

    private String buildDataId(String path, HttpServletRequest request) {
        List<NameValueDTO> dataKv = new ArrayList<>();
        request.getParameterMap().forEach((key, value) -> {
            NameValueDTO nameValueDTO = new NameValueDTO();
            nameValueDTO.setName(key);
            nameValueDTO.setValue(value[0]);
            dataKv.add(nameValueDTO);
        });
        String dataKvContent = MockConfigService.getDataKvContent(dataKv);
        String dataJson = "";
        try {
            dataJson = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return MockConfigService.buildDataId(path, dataKvContent, dataJson);
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
