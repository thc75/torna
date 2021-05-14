package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.EnumItem;
import cn.torna.dao.mapper.EnumItemMapper;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author tanghc
 */
@Service
public class EnumItemService extends BaseService<EnumItem, EnumItemMapper> {

    public EnumItem getByEnumIdAndName(long enumId, String name) {
        Query query = new Query()
                .eq("enum_id", enumId)
                .eq("name", name)
                .enableForceQuery()
                ;
        return get(query);
    }

}