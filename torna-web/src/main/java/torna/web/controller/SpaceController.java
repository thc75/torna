package torna.web.controller;

import torna.common.bean.Result;
import torna.common.bean.User;
import torna.common.context.UserContext;
import torna.dao.entity.Space;
import torna.service.SpaceService;
import torna.service.dto.SpaceAddDTO;
import torna.service.dto.SpaceInfoDTO;
import torna.service.dto.UserSpaceDTO;
import torna.web.controller.param.SpaceParam;
import torna.web.controller.param.SpaceUpdateParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("space")
public class SpaceController {

    @Autowired
    private SpaceService spaceService;

    @PostMapping("/updateName")
    public Result updateName(@RequestBody @Valid SpaceUpdateParam param) {
        Space space = spaceService.getById(param.getId());
        space.setName(param.getName());
        spaceService.updateIgnoreNull(space);
        return Result.ok();
    }

    @GetMapping("info")
    public Result<SpaceInfoDTO> getSpaceInfo(long spaceId) {
        SpaceInfoDTO spaceInfo = spaceService.getSpaceInfo(spaceId);
        return Result.ok(spaceInfo);
    }

    /**
     * 返回用户所在的空间
     * @return
     */
    @GetMapping("list")
    public Result<List<UserSpaceDTO>> listUserSpace() {
        User user = UserContext.getUser();
        return Result.ok(spaceService.listUserSpace(user));
    }

    /**
     * 添加空间
     * @param space
     * @return
     */
    @PostMapping("add")
    public Result add(@RequestBody SpaceAddDTO space) {
        User user = UserContext.getUser();
        space.setCreatorId(user.getUserId());
        space.setCreatorId(user.getUserId());
        spaceService.addSpace(space);
        return Result.ok();
    }

    @PostMapping("delete")
    public Result del(@RequestBody SpaceParam param) {
        Space space = spaceService.getById(param.getId());
        spaceService.delete(space);
        return Result.ok();
    }

}
