package cn.torna.service.metersphere;

import com.gitee.fastmybatis.core.support.BaseLambdaService;
import cn.torna.dao.entity.MsSpaceConfig;
import cn.torna.dao.mapper.MsSpaceConfigMapper;
import org.springframework.stereotype.Service;

/**
 * @author thc
 */
@Service
public class MsSpaceConfigService extends BaseLambdaService<MsSpaceConfig, MsSpaceConfigMapper> {

    public MsSpaceConfig getBySpaceId(Long spaceId) {
        return get(MsSpaceConfig::getSpaceId, spaceId);
    }

}
