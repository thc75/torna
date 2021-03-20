package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.MockConfig;
import cn.torna.dao.mapper.MockConfigMapper;
import cn.torna.service.dto.NameValueDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class MockConfigService extends BaseService<MockConfig, MockConfigMapper> {

    public List<MockConfig> listMockConfig(Long docId) {
        return this.list("doc_id", docId);
    }

    public MockConfig getMockConfig(long docId, String dataId) {
        Query query = new Query()
                .eq("doc_id", docId)
                .eq("data_id", dataId);
        return get(query);
    }

    public static String buildDataId(Long docId, List<NameValueDTO> nameValueDTOList, String body) {
        if (body == null) {
            body = "";
        }
        String queryString;
        if (CollectionUtils.isEmpty(nameValueDTOList)) {
            queryString = "";
        } else {
            queryString = nameValueDTOList.stream()
                    .sorted(Comparator.comparing(NameValueDTO::getValue))
                    .map(NameValueDTO::toString)
                    .collect(Collectors.joining("&"));
        }
        if (StringUtils.hasText(body)) {
            body = JSON.parseObject(body).toString(SerializerFeature.SortField);
        }
        String content = docId + queryString + body;
        return DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8));
    }

}