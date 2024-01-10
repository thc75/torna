package cn.torna.service.metersphere;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.MsSpaceConfig;
import cn.torna.dao.mapper.MsSpaceConfigMapper;
import org.springframework.stereotype.Service;

/**
 * @author thc
 */
@Service
public class MsSpaceConfigService extends BaseService<MsSpaceConfig, MsSpaceConfigMapper> {

    public MsSpaceConfig getBySpaceId(Long spaceId) {
        return get("space_id", spaceId);
    }

}
