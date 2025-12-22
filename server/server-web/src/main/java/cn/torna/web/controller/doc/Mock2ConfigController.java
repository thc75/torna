package cn.torna.web.controller.doc;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.enums.MockRequestDataTypeEnum;
import cn.torna.common.enums.MockResultTypeEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.MockConfig;
import cn.torna.service.MockConfigService;
import cn.torna.web.config.UserContext;
import cn.torna.web.controller.doc.param.MockConfigParam;
import cn.torna.web.controller.doc.vo.MockBaseVO;
import cn.torna.web.controller.doc.vo.MockConfigVO;
import cn.torna.web.controller.doc.vo.NameValueVO;
import cn.torna.web.controller.system.param.IdParam;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 高级mock
 *
 * @author tanghc
 */
@RestController
@RequestMapping("doc/mock2")
public class Mock2ConfigController {

    @Autowired
    private MockConfigService mockConfigService;

    /**
     * 获取
     */
    @GetMapping("list")
    public Result<List<MockConfigVO>> list(@HashId Long docId) {
        List<MockConfig> mockConfigs = mockConfigService.listAdvancedMockConfig(docId);
        List<MockConfigVO> configVOList = mockConfigs.stream()
                .map(this::convert)
                .collect(Collectors.toList());
        return Result.ok(configVOList);
    }

    private MockConfigVO convert(MockConfig mockConfig) {
        MockConfigVO mockConfigVO = new MockConfigVO();
        CopyUtil.copyPropertiesIgnoreNull(mockConfig, mockConfigVO);
        mockConfigVO.setResponseHeaders(JSON.parseArray(mockConfig.getResponseHeaders(), NameValueVO.class));
        if (mockConfig.getRequestDataType() == MockRequestDataTypeEnum.KV.getType()) {
            mockConfigVO.setDataKv(JSON.parseArray(mockConfig.getRequestData(), NameValueVO.class));
        } else {
            mockConfigVO.setDataJson(mockConfig.getRequestData());
        }
        if (StringUtils.hasText(mockConfig.getQueryData())) {
            mockConfigVO.setQuery(JSON.parseArray(mockConfig.getQueryData(), NameValueVO.class));
        }
        if (StringUtils.hasText(mockConfig.getHeaderData())) {
            mockConfigVO.setHeaders(JSON.parseArray(mockConfig.getHeaderData(), NameValueVO.class));
        }
        MockResultTypeEnum mockResponseBodyTypeEnum = StringUtils.hasText(mockConfig.getMockScript()) ?
                MockResultTypeEnum.SCRIPT : MockResultTypeEnum.CUSTOM;
        mockConfigVO.setResponseBodyType(mockResponseBodyTypeEnum.getType());
        return mockConfigVO;
    }


    /**
     * 保存mock
     */
    @PostMapping("save")
    public Result<MockBaseVO> save(@RequestBody MockConfigParam param) {
        User user = UserContext.getUser();
        String path = param.getPath();
        MockConfig mockConfig = param.getId() == null ? null : mockConfigService.getById(param.getId());
        boolean save = false;
        if (mockConfig == null) {
            checkCount(param.getDocId());
            mockConfig = new MockConfig();
            mockConfig.setDocId(param.getDocId());
            mockConfig.setCreatorId(user.getUserId());
            mockConfig.setCreatorName(user.getNickname());
            mockConfig.setVersion(param.getVersion());
            save = true;
        }
        String dataId = buildDataId(param);
        mockConfig.setDataId(dataId);
        mockConfig.setName(param.getName());
        mockConfig.setPath(path);
        mockConfig.setHttpMethod(param.getHttpMethod());
        mockConfig.setRequestDataType(param.getRequestDataType());
        mockConfig.setIsFilterIp(param.getIsFilterIp());
        mockConfig.setIp(param.getIp());
        mockConfig.setRequestData(getRequestData(param));
        mockConfig.setQueryData(JSON.toJSONString(param.getQuery()));
        mockConfig.setHeaderData(JSON.toJSONString(param.getHeaders()));
        mockConfig.setHttpStatus(param.getHttpStatus());
        mockConfig.setDelayMills(param.getDelayMills());
        mockConfig.setResultType(param.getResultType());
        mockConfig.setResponseHeaders(JSON.toJSONString(param.getResponseHeaders()));
        mockConfig.setResponseBody(param.getResponseBody());
        mockConfig.setMockScript(param.getMockScript());
        mockConfig.setMockResult(param.getMockResult());
        mockConfig.setRemark(param.getRemark());
        mockConfig.setModifierId(user.getUserId());
        mockConfig.setModifierName(user.getNickname());
        mockConfig.setIsAdvancedMock(Booleans.TRUE);
        if (save) {
            mockConfigService.save(mockConfig);
        } else {
            mockConfigService.update(mockConfig);
        }
        MockBaseVO mockBaseVO = CopyUtil.copyBean(mockConfig, MockBaseVO::new);
        return Result.ok(mockBaseVO);
    }

    private void checkCount(Long docId) {
        if (mockConfigService.getAdvancedMockConfigCount(docId) >= 3) {
            throw new BizException("最多配置3个");
        }
    }

    private String buildDataId(MockConfigParam param) {
        String path = param.getPath();
        return MockConfigService.buildDataId(path);
    }

    private String getRequestData(MockConfigParam param) {
        String data;
        MockRequestDataTypeEnum mockParamTypeEnum = MockRequestDataTypeEnum.of(param.getRequestDataType());
        switch (mockParamTypeEnum) {
            case KV:
                data = MockConfigService.getDataKvContent(param.getDataKv());
                break;
            case JSON:
                data = param.getDataJson();
                break;
            default:
                throw new IllegalArgumentException("error MockParamTypeEnum");
        }
        return data;
    }

    /**
     * 删除mock
     */
    @PostMapping("delete")
    public Result delete(@RequestBody IdParam param) {
        User user = UserContext.getUser();
        MockConfig mockConfig = mockConfigService.getById(param.getId());
        if (mockConfig != null) {
            mockConfig.setModifierId(user.getUserId());
            mockConfig.setModifierName(user.getNickname());
            mockConfigService.delete(mockConfig);
        }
        return Result.ok();
    }


    @GetMapping("version/next")
    public Result<Integer> getNextVersion(@HashId Long docId) {
        checkCount(docId);
        int nextVersion = mockConfigService.getAdvancedNextVersion(docId);
        return Result.ok(nextVersion);
    }

}
