package cn.torna.service.metersphere;

import com.gitee.fastmybatis.core.support.BaseLambdaService;
import cn.torna.dao.entity.MsModuleConfig;
import cn.torna.dao.mapper.MsModuleConfigMapper;
import org.springframework.stereotype.Service;

/**
 * @author thc
 */
@Service
public class MsModuleConfigService extends BaseLambdaService<MsModuleConfig, MsModuleConfigMapper> {

    public MsModuleConfig getByModuleId(Long moduleId) {
        return get(MsModuleConfig::getModuleId, moduleId);
    }

}
