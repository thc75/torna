package cn.torna.service;

import cn.torna.common.bean.Booleans;
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
        Query query = new Query()
                .eq("module_id", moduleId);
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
        Query query = new Query()
                .eq("module_id", moduleId);
        List<EnumInfo> enumInfoList = enumInfoService.list(query);
        return CopyUtil.copyList(enumInfoList, EnumInfoDTO::new);
    }


    @Transactional(rollbackFor = Exception.class)
    public EnumInfo saveEnumInfo(EnumInfoDTO enumInfoDTO) {
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
        if (items != null) {
            for (EnumItemDTO item : items) {
                item.setEnumId(enumInfo.getId());
                this.saveEnumItem(item);
            }
        }
    }

    public EnumInfo addEnumInfo(EnumInfoDTO enumInfoDTO) {
        this.checkInfoExist(enumInfoDTO);
        String dataId = enumInfoDTO.buildDataId();
        EnumInfo enumInfo  = CopyUtil.copyBean(enumInfoDTO, EnumInfo::new);
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
        Query query = new Query()
                .eq("enum_id", enumId)
                .orderby("id", Sort.ASC);
        List<EnumItem> itemList = enumItemService.list(query);
        return CopyUtil.copyList(itemList, EnumItemDTO::new);
    }

    private void saveEnumItem(EnumItemDTO itemDTO) {
        EnumItem enumItem = enumItemService.getByEnumIdAndName(itemDTO.getEnumId(), itemDTO.getName());
        if (enumItem == null) {
            enumItem = CopyUtil.copyBean(itemDTO, EnumItem::new);
            enumItem.setIsDeleted(Booleans.FALSE);
            enumItemService.save(enumItem);
        } else {
            CopyUtil.copyPropertiesIgnoreNull(itemDTO, enumItem);
            enumItem.setIsDeleted(Booleans.FALSE);
            enumItemService.update(enumItem);
        }
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
        Query query = new Query()
                .eq("module_id", enumInfoDTO.getModuleId())
                .eq("name", enumInfoDTO.getName());
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