package cn.torna.web.controller.doc;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.enums.MockRequestDataTypeEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.MockConfig;
import cn.torna.service.MockConfigService;
import cn.torna.service.dto.NameValueDTO;
import cn.torna.web.controller.doc.param.MockConfigParam;
import cn.torna.web.controller.doc.vo.MockBaseVO;
import cn.torna.web.controller.doc.vo.MockConfigVO;
import cn.torna.web.controller.doc.vo.NameValueVO;
import cn.torna.web.controller.system.param.IdParam;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("doc/mock")
public class MockConfigController {

    @Autowired
    private MockConfigService mockConfigService;


    /**
     * 获取
     */
    @GetMapping("list")
    public Result<List<MockConfigVO>> list(@HashId Long docId) {
        List<MockConfig> mockConfigs = mockConfigService.listMockConfig(docId);
        List<MockConfigVO> configVOList = mockConfigs.stream()
                .map(this::convert)
                .collect(Collectors.toList());
        return Result.ok(configVOList);
    }

    private MockConfigVO convert(MockConfig mockConfig) {
        MockConfigVO mockConfigVO = CopyUtil.copyBean(mockConfig, MockConfigVO::new);
        switch (MockRequestDataTypeEnum.of(mockConfigVO.getRequestDataType())) {
            case KV:
                mockConfigVO.setDataKv(JSON.parseArray(mockConfig.getRequestData(), NameValueVO.class));
                mockConfigVO.setDataJson("");
                break;
            case JSON:
                mockConfigVO.setDataKv(Collections.emptyList());
                mockConfigVO.setDataJson(mockConfig.getRequestData());
                break;
        }
        mockConfigVO.setResponseHeaders(JSON.parseArray(mockConfig.getResponseHeaders(), NameValueVO.class));
        return mockConfigVO;
    }


    /**
     * 保存mock
     */
    @PostMapping("save")
    public Result<MockBaseVO> save(@RequestBody MockConfigParam param) {
        User user = UserContext.getUser();
        String dataId = buildDataId(param);
        MockConfig mockConfig = mockConfigService.getById(param.getId());
        boolean save = false;
        if (mockConfig == null) {
            mockConfig = new MockConfig();
            mockConfig.setDocId(param.getDocId());
            mockConfig.setCreatorId(user.getUserId());
            mockConfig.setCreatorName(user.getNickname());
            save = true;
        }
        mockConfig.setName(param.getName());
        mockConfig.setDataId(dataId);
        mockConfig.setRequestDataType(param.getRequestDataType());
        mockConfig.setRequestData(getRequestData(param));
        mockConfig.setHttpStatus(param.getHttpStatus());
        mockConfig.setDelayMills(param.getDelayMills());
        mockConfig.setResponseHeaders(JSON.toJSONString(param.getResponseHeaders()));
        mockConfig.setResponseBody(param.getResponseBody());
        mockConfig.setRemark(param.getRemark());
        mockConfig.setModifierId(user.getUserId());
        mockConfig.setModifierName(user.getNickname());
        if (save) {
            mockConfigService.save(mockConfig);
        } else {
            mockConfigService.update(mockConfig);
        }
        MockBaseVO mockBaseVO = CopyUtil.copyBean(mockConfig, MockBaseVO::new);
        return Result.ok(mockBaseVO);
    }

    private String getRequestData(MockConfigParam param) {
        String data;
        MockRequestDataTypeEnum mockParamTypeEnum = MockRequestDataTypeEnum.of(param.getRequestDataType());
        switch (mockParamTypeEnum) {
            case KV:
                data = JSON.toJSONString(param.getDataKv());
                break;
            case JSON:
                data = param.getDataJson();
                break;
            default:
                throw new IllegalArgumentException("error MockParamTypeEnum");
        }
        return data;
    }

    private String buildDataId(MockConfigParam param) {
        Byte paramType = param.getRequestDataType();
        Long docId = param.getDocId();
        MockRequestDataTypeEnum mockParamTypeEnum = MockRequestDataTypeEnum.of(paramType);
        switch (mockParamTypeEnum) {
            case KV:
                List<NameValueDTO> requestData = param.getDataKv();
                return MockConfigService.buildDataId(docId, requestData, "");
            case JSON:
                return MockConfigService.buildDataId(docId, null, param.getDataJson());
            default:
                throw new IllegalArgumentException("error MockParamTypeEnum");
        }
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



}