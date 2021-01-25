package cn.torna.web.controller.doc;

import cn.torna.common.bean.Result;
import cn.torna.service.EnumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.torna.common.annotation.HashId;
import cn.torna.service.dto.EnumInfoDTO;
import cn.torna.service.dto.EnumItemDTO;

import java.util.List;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("doc/enum")
public class EnumController {

    @Autowired
    private EnumService enumService;

    @GetMapping("info/baselist")
    public Result<List<EnumInfoDTO>> baselist(@HashId Long moduleId) {
        List<EnumInfoDTO> enumInfoDTOS = enumService.listBase(moduleId);
        return Result.ok(enumInfoDTOS);
    }

    @GetMapping("info/list")
    public Result<List<EnumInfoDTO>> list(@HashId Long moduleId) {
        List<EnumInfoDTO> enumInfoDTOS = enumService.listEnumInfo(moduleId);
        return Result.ok(enumInfoDTOS);
    }

    @PostMapping("info/add")
    public Result add(@RequestBody EnumInfoDTO enumInfoDTO) {
        enumService.addEnumInfo(enumInfoDTO);
        return Result.ok();
    }

    @GetMapping("info/delete")
    public Result delete(@HashId Long id) {
        enumService.deleteEnumInfo(id);
        return Result.ok();
    }

    @PostMapping("info/update")
    public Result update(@RequestBody EnumInfoDTO enumInfoDTO) {
        enumService.updateEnumInfo(enumInfoDTO);
        return Result.ok();
    }

    @GetMapping("item/list")
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
