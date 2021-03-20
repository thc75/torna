package cn.torna.web.controller.mock;

import cn.torna.common.util.IdUtil;
import cn.torna.common.util.ResponseUtil;
import cn.torna.dao.entity.MockConfig;
import cn.torna.service.MockConfigService;
import cn.torna.service.dto.NameValueDTO;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author tanghc
 */
@Controller
@RequestMapping("mock")
@Slf4j
public class MockController {

    @Autowired
    private MockConfigService mockConfigService;

    @RequestMapping("{docIdHash}")
    public void mock(
            @PathVariable("docIdHash") String docIdHash,
            HttpServletRequest request, HttpServletResponse response) {
        Long docId = IdUtil.decode(docIdHash);
        if (docId == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        String dataId = this.buildDataId(docId, request);
        MockConfig mockConfig = mockConfigService.getMockConfig(docId, dataId);
        if (mockConfig == null) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            ResponseUtil.writeText(response, "mock未配置");
            return;
        }
        this.delay(mockConfig);
        this.setResponseHeaders(response, mockConfig);
        response.setStatus(mockConfig.getHttpStatus());
        String responseBody = mockConfig.getResponseBody();
        ResponseUtil.write(response, responseBody);
    }

    private void delay(MockConfig mockConfig) {
        Integer delayMills = mockConfig.getDelayMills();
        // 延迟返回
        if (delayMills > 0) {
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

    private String buildDataId(Long docId, HttpServletRequest request) {
        String body;
        try {
            body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("获取body出错，url:{}", request.getRequestURI(), e);
            body = "";
        }
        Enumeration<String> parameterNames = request.getParameterNames();
        List<NameValueDTO> nameValueDTOList = new ArrayList<>(8);
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String value = request.getParameter(name);
            NameValueDTO nameValueDTO = new NameValueDTO();
            nameValueDTO.setName(name);
            nameValueDTO.setValue(value);
            nameValueDTOList.add(nameValueDTO);
        }
        return MockConfigService.buildDataId(docId, nameValueDTOList, body);
    }

}
