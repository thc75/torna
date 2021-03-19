package cn.torna.service;

import cn.torna.common.bean.User;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.MockConfig;
import cn.torna.dao.mapper.MockConfigMapper;
import cn.torna.service.dto.MockDTO;
import cn.torna.service.dto.NameValueDTO;
import com.alibaba.fastjson.JSON;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class MockConfigService extends BaseService<MockConfig, MockConfigMapper> {

    public List<MockDTO> listMockConfig(Long docId) {
        List<MockConfig> mockConfigList = this.list("doc_id", docId);
        return mockConfigList.stream()
                .map(mockConfig -> {
                    MockDTO mockDTO = CopyUtil.copyBean(mockConfig, MockDTO::new);
                    List<NameValueDTO> queryData = JSON.parseArray(mockConfig.getQueryString(), NameValueDTO.class);
                    mockDTO.setQueryData(queryData);
                    List<NameValueDTO> respHeaders = JSON.parseArray(mockConfig.getResponseHeaders(), NameValueDTO.class);
                    mockDTO.setResponseHeaders(respHeaders);
                    return mockDTO;
                })
                .collect(Collectors.toList());
    }


    public void saveMock(MockDTO mockDTO, User user) {
        String dataId = buildDataId(mockDTO.getDocId(), mockDTO.getQueryData());
        MockConfig mockConfig = this.getById(mockDTO.getId());
        boolean save = false;
        if (mockConfig == null) {
            mockConfig = new MockConfig();
            mockConfig.setDocId(mockDTO.getDocId());
            mockConfig.setCreatorId(user.getUserId());
            mockConfig.setCreatorName(user.getNickname());
            save = true;
        }
        mockConfig.setName(mockDTO.getName());
        mockConfig.setDataId(dataId);
        mockConfig.setQueryString(JSON.toJSONString(mockDTO.getQueryData()));
        mockConfig.setHttpStatus(mockDTO.getHttpStatus());
        mockConfig.setDelayMills(mockDTO.getDelayMills());
        mockConfig.setResponseHeaders(JSON.toJSONString(mockDTO.getResponseHeaders()));
        mockConfig.setResponseBody(mockDTO.getResponseBody());
        mockConfig.setRemark(mockDTO.getRemark());
        mockConfig.setModifierId(user.getUserId());
        mockConfig.setModifierName(user.getNickname());
        if (save) {
            this.save(mockConfig);
        } else {
            this.update(mockConfig);
        }
        mockDTO.setId(mockConfig.getId());
    }

    public MockConfig getMockConfig(long docId, String dataId) {
        Query query = new Query()
                .eq("doc_id", docId)
                .eq("data_id", dataId);
        return get(query);
    }

    public static String buildDataId(Long docId, List<NameValueDTO> nameValueDTOList) {
        String queryString;
        if (CollectionUtils.isEmpty(nameValueDTOList)) {
            queryString = "";
        } else {
            queryString = nameValueDTOList.stream()
                    .sorted(Comparator.comparing(NameValueDTO::getValue))
                    .map(NameValueDTO::toString)
                    .collect(Collectors.joining("&"));
        }
        String content = docId + queryString;
        return DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8));
    }

}