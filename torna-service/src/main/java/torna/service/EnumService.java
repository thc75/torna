package torna.service;

import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import torna.common.bean.Booleans;
import torna.common.exception.BizException;
import torna.common.support.BaseService;
import torna.common.util.CopyUtil;
import torna.dao.entity.EnumInfo;
import torna.dao.entity.EnumItem;
import torna.dao.mapper.EnumInfoMapper;
import torna.service.dto.EnumInfoDTO;
import torna.service.dto.EnumItemDTO;

import java.util.Date;
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

    public List<EnumInfoDTO> listBase(long moduleId) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .orderby("id", Sort.DESC);
        List<EnumInfo> enumInfoList = enumInfoService.list(query);
        return CopyUtil.copyList(enumInfoList, EnumInfoDTO::new);
    }

    public List<EnumInfoDTO> listEnumInfo(long moduleId) {
        List<EnumInfoDTO> enumInfoDTOS = this.listBase(moduleId);
        for (EnumInfoDTO enumInfoDTO : enumInfoDTOS) {
            List<EnumItemDTO> enumItemDTOS = this.listItems(enumInfoDTO.getId());
            enumInfoDTO.setItems(enumItemDTOS);
        }
        return enumInfoDTOS;
    }

    public EnumInfo addEnumInfo(EnumInfoDTO enumInfoDTO) {
        this.checkInfoExist(enumInfoDTO);
        EnumInfo enumInfo = CopyUtil.copyBean(enumInfoDTO, EnumInfo::new);
        enumInfoService.saveIgnoreNull(enumInfo);
        return enumInfo;
    }

    public EnumInfo updateEnumInfo(EnumInfoDTO enumInfoDTO) {
        this.checkInfoExist(enumInfoDTO);
        EnumInfo enumInfo = CopyUtil.copyBean(enumInfoDTO, EnumInfo::new);
        enumInfoService.updateIgnoreNull(enumInfo);
        return enumInfo;
    }

    public List<EnumItemDTO> listItems(long enumId) {
        Query query = new Query()
                .eq("enum_id", enumId)
                .orderby("id", Sort.ASC);
        List<EnumItem> itemList = enumItemService.list(query);
        return CopyUtil.copyList(itemList, EnumItemDTO::new);
    }

    public EnumItem addEnumItem(EnumItemDTO itemDTO) {
        this.checkItemExist(itemDTO);
        EnumItem enumItem = CopyUtil.copyBean(itemDTO, EnumItem::new);
        enumItemService.saveIgnoreNull(enumItem);
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
        Query query = new Query()
                .eq("enum_id", itemDTO.getEnumId())
                .eq("name", itemDTO.getName())
                ;
        EnumItem enumItem = enumItemService.get(query);
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
        enumItemService.delete(item);
    }
}