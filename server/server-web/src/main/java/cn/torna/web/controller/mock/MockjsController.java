package cn.torna.web.controller.mock;

import cn.torna.common.annotation.NoLogin;
import cn.torna.common.util.IdUtil;
import cn.torna.common.util.ResponseUtil;
import cn.torna.dao.entity.MockConfig;
import cn.torna.service.MockConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tanghc
 */
@Controller
@RequestMapping("mockjs")
@NoLogin
public class MockjsController {

    private static final String PREFIX = "/mockjs/script/";

    @Autowired
    private MockConfigService mockConfigService;

    @Value("${torna.mock.ignore-param:false}")
    private boolean ignoreParam;

    @RequestMapping("/script/**")
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
            ResponseUtil.writeText(response, "script not found");
            return;
        }
        String mockScript = mockConfig.getMockScript();
        ResponseUtil.writeText(response, mockScript);
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


}
