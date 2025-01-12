package cn.torna.service;

import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.EnumInfo;
import cn.torna.dao.entity.EnumItem;
import cn.torna.service.dto.EnumInfoDTO;
import cn.torna.service.dto.EnumItemDTO;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author tanghc
 */
@Service
public class EnumService {

    @Autowired
    private EnumInfoService enumInfoService;

    @Autowired
    private EnumItemService enumItemService;

    public List<EnumInfoDTO> listAll(long moduleId) {
        Query query = enumInfoService.query()
                .eq(EnumInfo::getModuleId, moduleId);
        List<EnumInfo> enumInfoList = enumInfoService.list(query);
        List<EnumInfoDTO> enumInfoDTOS = CopyUtil.copyList(enumInfoList, EnumInfoDTO::new);
        for (EnumInfoDTO enumInfoDTO : enumInfoDTOS) {
            Long enumId = enumInfoDTO.getId();
            List<EnumItemDTO> items = this.listItems(enumId);
            enumInfoDTO.setItems(items);
        }
        return enumInfoDTOS;
    }

    public List<EnumInfoDTO> listBase(long moduleId) {
        Query query = enumInfoService.query()
                .eq(EnumInfo::getModuleId, moduleId);
        List<EnumInfo> enumInfoList = enumInfoService.list(query);
        return CopyUtil.copyList(enumInfoList, EnumInfoDTO::new);
    }


    @Transactional(rollbackFor = Exception.class)
    public EnumInfo saveEnumInfo(EnumInfoDTO enumInfoDTO) {
        // 如果枚举名称为空则使用字段名称
        if (StringUtils.isEmpty(enumInfoDTO.getName())) {
            enumInfoDTO.setName(enumInfoDTO.getDescription());
        }
        String dataId = enumInfoDTO.buildDataId();
        EnumInfo enumInfo = enumInfoService.getByDataId(dataId);
        if (enumInfo == null) {
            enumInfo = CopyUtil.copyBean(enumInfoDTO, EnumInfo::new);
            enumInfo.setDataId(dataId);
            enumInfoService.save(enumInfo);
        } else {
            enumInfo.setDescription(enumInfoDTO.getDescription());
            enumInfoService.update(enumInfo);
        }
        List<EnumItemDTO> items = enumInfoDTO.getItems();
        this.updateItems(enumInfo, items);
        return enumInfo;
    }

    private void updateItems(EnumInfo enumInfo, List<EnumItemDTO> items) {
        if (!CollectionUtils.isEmpty(items)) {
            enumItemService.replaceEnumItem(enumInfo.getId(), items);
        }
    }

    public EnumInfo addEnumInfo(EnumInfoDTO enumInfoDTO) {
        this.checkInfoExist(enumInfoDTO);
        String dataId = enumInfoDTO.buildDataId();
        EnumInfo enumInfo = CopyUtil.copyBean(enumInfoDTO, EnumInfo::new);
        enumInfo.setDataId(dataId);
        enumInfoService.save(enumInfo);
        return enumInfo;
    }

    public EnumInfo updateEnumInfo(EnumInfoDTO enumInfoDTO) {
        this.checkInfoExist(enumInfoDTO);
        EnumInfo enumInfo = CopyUtil.copyBean(enumInfoDTO, EnumInfo::new);
        enumInfo.setDataId(enumInfoDTO.buildDataId());
        enumInfoService.update(enumInfo);
        return enumInfo;
    }

    public List<EnumItemDTO> listItems(long enumId) {
        Query query = enumItemService.query()
                .eq(EnumItem::getEnumId, enumId)
                .orderBy(EnumItem::getId, Sort.ASC);
        List<EnumItem> itemList = enumItemService.list(query);
        return CopyUtil.copyList(itemList, EnumItemDTO::new);
    }

    public EnumItem addEnumItem(EnumItemDTO itemDTO) {
        this.checkItemExist(itemDTO);
        EnumItem enumItem = CopyUtil.copyBean(itemDTO, EnumItem::new);
        enumItemService.save(enumItem);
        return enumItem;
    }

    public void addEnumItems(List<EnumItem> items) {
        if (CollectionUtils.isEmpty(items)) {
            return;
        }
        enumItemService.saveBatch(items);
    }

    private void checkInfoExist(EnumInfoDTO enumInfoDTO) {
        Query query = enumInfoService.query()
                .eq(EnumInfo::getModuleId, enumInfoDTO.getModuleId())
                .eq(EnumInfo::getName, enumInfoDTO.getName());
        EnumInfo enumInfo = enumInfoService.get(query);
        if (enumInfo != null) {
            if (enumInfo.getId() == null || !enumInfo.getId().equals(enumInfoDTO.getId())) {
                throw new BizException(enumInfoDTO.getName() + "已存在");
            }
        }
    }

    private void checkItemExist(EnumItemDTO itemDTO) {
        EnumItem enumItem = enumItemService.getByEnumIdAndName(itemDTO.getEnumId(), itemDTO.getName());
        if (enumItem != null) {
            if (enumItem.getId() == null || !enumItem.getId().equals(itemDTO.getId())) {
                throw new BizException(itemDTO.getName() + "已存在");
            }
        }
    }

    public EnumItem updateItem(EnumItemDTO itemDTO) {
        this.checkItemExist(itemDTO);
        EnumItem enumItem = enumItemService.getById(itemDTO.getId());
        CopyUtil.copyPropertiesIgnoreNull(itemDTO, enumItem);
        enumItemService.update(enumItem);
        return enumItem;
    }

    public void deleteEnumInfo(long id) {
        EnumInfo enumInfo = enumInfoService.getById(id);
        enumInfoService.delete(enumInfo);
    }

    public void deleteEnumItem(long id) {
        EnumItem item = enumItemService.getById(id);
        enumItemService.getMapper().forceDelete(item);
    }
}
