package cn.torna.service;

import cn.torna.dao.entity.EnumItem;
import cn.torna.dao.mapper.EnumItemMapper;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import org.springframework.stereotype.Service;

/**
 * @author tanghc
 */
@Service
public class EnumItemService extends BaseLambdaService<EnumItem, EnumItemMapper> {

    public EnumItem getByEnumIdAndName(long enumId, String name) {
        return this.query()
                .eq(EnumItem::getEnumId, enumId)
                .eq(EnumItem::getName, name)
                .ignoreLogicDeleteColumn()
                .get();
    }

}
