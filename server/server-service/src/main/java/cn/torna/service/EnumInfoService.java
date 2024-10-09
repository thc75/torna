package cn.torna.service;

import com.gitee.fastmybatis.core.support.BaseLambdaService;
import cn.torna.dao.entity.EnumInfo;
import cn.torna.dao.mapper.EnumInfoMapper;
import org.springframework.stereotype.Service;

/**
 * @author tanghc
 */
@Service
public class EnumInfoService extends BaseLambdaService<EnumInfo, EnumInfoMapper> {

    public EnumInfo getByDataId(String dataId) {
        return get(EnumInfo::getDataId, dataId);
    }
}
