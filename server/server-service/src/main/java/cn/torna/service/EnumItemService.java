package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.EnumItem;
import cn.torna.dao.mapper.EnumItemMapper;
import cn.torna.service.dto.EnumItemDTO;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import java.util.List;
import java.util.stream.Collectors;
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

    public void replaceEnumItem(long enumId, List<EnumItemDTO> items) {
        // 删除枚举子项
        forceDeleteEnumItem(enumId);

        // 批量保存
        this.saveBatch(enumId, items);
    }

    public void forceDeleteEnumItem(long enumId) {
        Query delQuery = this.query().eq(EnumItem::getEnumId, enumId);
        this.forceDeleteByQuery(delQuery);
    }

    private void saveBatch(long enumId, List<EnumItemDTO> itemDTOList) {
        List<EnumItem> enumItemList = itemDTOList.stream().map(enumItemDTO -> {
            EnumItem enumItem = CopyUtil.copyBean(enumItemDTO, EnumItem::new);
            enumItem.setEnumId(enumId);
            enumItem.setIsDeleted(Booleans.FALSE);
            return enumItem;
        }).collect(Collectors.toList());

        this.saveBatch(enumItemList);
    }

}
