package cn.torna.web.controller.mock;

import cn.torna.common.enums.MockResultTypeEnum;
import cn.torna.common.util.IdUtil;
import cn.torna.common.util.ResponseUtil;
import cn.torna.dao.entity.MockConfig;
import cn.torna.service.MockConfigService;
import cn.torna.service.dto.NameValueDTO;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author tanghc
 */
@Controller
@RequestMapping("mock")
@Slf4j
public class MockController {

    public static final int ONE_MINUTE_MILLS = 60000;

    @Autowired
    private MockConfigService mockConfigService;

    @RequestMapping("{mockId}")
    public void mock(
            @PathVariable("mockId") String mockId, HttpServletResponse response) {
        Long id = IdUtil.decode(mockId);
        if (id == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            ResponseUtil.writeText(response, "mock地址不存在");
            return;
        }
        MockConfig mockConfig = mockConfigService.getById(id);
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
