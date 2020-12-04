package torna.api.open;

import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import org.springframework.beans.factory.annotation.Autowired;
import torna.api.bean.RequestContext;
import torna.api.open.param.EnumInfoCreateParam;
import torna.api.open.param.EnumItemCreateParam;
import torna.api.open.result.EnumInfoCreateResult;
import torna.common.bean.Booleans;
import torna.common.util.CopyUtil;
import torna.dao.entity.EnumInfo;
import torna.dao.entity.EnumItem;
import torna.service.EnumService;
import torna.service.dto.EnumInfoDTO;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@ApiService
@ApiDoc(value = "字典API")
public class EnumApi {

    @Autowired
    private EnumService enumService;

    @Api(name = "enum.push")
    @ApiDocMethod(description = "推送字典")
    public EnumInfoCreateResult push(EnumInfoCreateParam param) {
        long moduleId = RequestContext.getCurrentContext().getModuleId();
        EnumInfoDTO enumInfoDTO = CopyUtil.copyBean(param, EnumInfoDTO::new);
        enumInfoDTO.setModuleId(moduleId);
        // 创建分类
        EnumInfo enumInfo = enumService.addEnumInfo(enumInfoDTO);
        // 创建字典项
        List<EnumItemCreateParam> items = param.getItems();
                Date now = new Date();
        List<EnumItem> itemList = items.stream()
                .map(enumItemDTO -> {
                    EnumItem item = CopyUtil.copyBean(enumItemDTO, EnumItem::new);
                    item.setEnumId(enumInfo.getId());
                    item.setIsDeleted(Booleans.FALSE);
                    item.setGmtCreate(now);
                    return item;
                })
                .collect(Collectors.toList());
        enumService.addEnumItems(itemList);
        return CopyUtil.copyBean(enumInfo, EnumInfoCreateResult::new);
    }

}
