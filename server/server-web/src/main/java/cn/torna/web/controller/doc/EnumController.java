package cn.torna.web.controller.doc;

import cn.torna.common.annotation.HashId;
import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.Result;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.EnumInfo;
import cn.torna.service.EnumService;
import cn.torna.service.dto.EnumInfoDTO;
import cn.torna.service.dto.EnumItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("doc/enum")
public class EnumController {

    @Autowired
    private EnumService enumService;

    @GetMapping("info/all")
    @NoLogin
    public Result<List<EnumInfoDTO>> all(@HashId Long moduleId) {
        List<EnumInfoDTO> enumInfoDTOS = enumService.listAll(moduleId);
        return Result.ok(enumInfoDTOS);
    }

    @GetMapping("info/baselist")
    public Result<List<EnumInfoDTO>> listBase(@HashId Long moduleId) {
        List<EnumInfoDTO> enumInfoDTOS = enumService.listBase(moduleId);
        return Result.ok(enumInfoDTOS);
    }

    @PostMapping("info/add")
    public Result<EnumInfoDTO> add(@RequestBody EnumInfoDTO enumInfoDTO) {
        EnumInfo enumInfo = enumService.addEnumInfo(enumInfoDTO);
        EnumInfoDTO ret = CopyUtil.copyBean(enumInfo, EnumInfoDTO::new);
        return Result.ok(ret);
    }

    @GetMapping("info/delete")
    public Result delete(@HashId Long id) {
        enumService.deleteEnumInfo(id);
        return Result.ok();
    }

    @PostMapping("info/update")
    public Result<EnumInfoDTO> update(@RequestBody EnumInfoDTO enumInfoDTO) {
        EnumInfo enumInfo = enumService.updateEnumInfo(enumInfoDTO);
        EnumInfoDTO ret = CopyUtil.copyBean(enumInfo, EnumInfoDTO::new);
        return Result.ok(ret);
    }

    @GetMapping("item/list")
    @NoLogin
    public Result<List<EnumItemDTO>> listItem(@HashId Long enumId) {
        List<EnumItemDTO> enumItemDTOS = enumService.listItems(enumId);
        return Result.ok(enumItemDTOS);
    }

    @PostMapping("item/add")
    public Result addItem(@RequestBody EnumItemDTO enumItemDTO) {
        enumService.addEnumItem(enumItemDTO);
        return Result.ok();
    }

    @PostMapping("item/update")
    public Result updateItem(@RequestBody EnumItemDTO enumItemDTO) {
        enumService.updateItem(enumItemDTO);
        return Result.ok();
    }

    @GetMapping("item/delete")
    public Result deleteItem(@HashId Long id) {
        enumService.deleteEnumItem(id);
        return Result.ok();
    }

}
