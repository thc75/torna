package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.EnumInfo;
import cn.torna.dao.mapper.EnumInfoMapper;
import org.springframework.stereotype.Service;

/**
 * @author tanghc
 */
@Service
public class EnumInfoService extends BaseService<EnumInfo, EnumInfoMapper> {

    public EnumInfo getByDataId(String dataId) {
        return get("data_id", dataId);
    }
}