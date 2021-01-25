package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.EnumInfo;
import cn.torna.dao.entity.EnumItem;
import cn.torna.dao.mapper.EnumInfoMapper;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import cn.torna.common.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.torna.service.dto.EnumInfoDTO;
import cn.torna.service.dto.EnumItemDTO;

import java.util.List;

/**
 * @author tanghc
 */
@Service
public class EnumInfoService extends BaseService<EnumInfo, EnumInfoMapper> {

    @Autowired
    private EnumItemService enumItemService;

    public List<EnumInfoDTO> listBase(long moduleId) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .orderby("id", Sort.DESC);
        List<EnumInfo> enumInfoList = this.list(query);
        return CopyUtil.copyList(enumInfoList, EnumInfoDTO::new);
    }

    public EnumInfo getByDataId(String dataId) {
        return get("data_id", dataId);
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
        this.save(enumInfo);
        return enumInfo;
    }

    public EnumInfo updateEnumInfo(EnumInfoDTO enumInfoDTO) {
        this.checkInfoExist(enumInfoDTO);
        EnumInfo enumInfo = CopyUtil.copyBean(enumInfoDTO, EnumInfo::new);
        this.update(enumInfo);
        return enumInfo;
    }

    public List<EnumItemDTO> listItems(long enumId) {
        Query query = new Query()
                .eq("enum_id", enumId)
                .orderby("id", Sort.ASC);
        List<EnumItem> itemList = enumItemService.list(query);
        return CopyUtil.copyList(itemList, EnumItemDTO::new);
    }

    public EnumItem add(EnumItemDTO itemDTO) {
        this.checkItemExist(itemDTO);
        EnumItem enumItem = CopyUtil.copyBean(itemDTO, EnumItem::new);
        enumItemService.save(enumItem);
        return enumItem;
    }

    private void checkInfoExist(EnumInfoDTO enumInfoDTO) {
        Query query = new Query()
                .eq("module_id", enumInfoDTO.getModuleId())
                .eq("name", enumInfoDTO.getName());
        EnumInfo enumInfo = this.get(query);
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
        EnumInfo enumInfo = getById(id);
        delete(enumInfo);
    }

    public void deleteEnumItem(long id) {
        EnumItem item = enumItemService.getById(id);
        enumItemService.delete(item);
    }
}