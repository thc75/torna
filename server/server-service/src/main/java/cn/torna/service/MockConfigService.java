package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.MockConfig;
import cn.torna.dao.mapper.MockConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tanghc
 */
@Service
public class MockConfigService extends BaseService<MockConfig, MockConfigMapper> {

    public List<MockConfig> listMockConfig(Long docId) {
        return this.list("doc_id", docId);
    }

}