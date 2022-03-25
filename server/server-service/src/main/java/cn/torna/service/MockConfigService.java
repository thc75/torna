package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.MockConfig;
import cn.torna.dao.mapper.MockConfigMapper;
import cn.torna.service.dto.NameValueDTO;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

/**
 * @author tanghc
 */
@Service
public class MockConfigService extends BaseService<MockConfig, MockConfigMapper> {

    public List<MockConfig> listMockConfig(Long docId) {
        return this.list("doc_id", docId);
    }

    public MockConfig getByDataId(String dataId) {
        return get("data_id", dataId);
    }

    public static String getDataKvContent(List<NameValueDTO> dataKv) {
        if (CollectionUtils.isEmpty(dataKv)) {
            return "[]";
        }
        dataKv.sort(Comparator.comparing(NameValueDTO::getName));
        return JSON.toJSONString(dataKv);
    }

    public static String buildDataId(String path, String dataKvContent, String dataJson) {
        return DigestUtils.md5DigestAsHex(path.getBytes(StandardCharsets.UTF_8));
    }

    public static String buildDataId(String path) {
        return buildDataId(path, "", "");
    }

}