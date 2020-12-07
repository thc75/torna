package torna.web.controller.space;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import torna.common.annotation.HashId;
import torna.common.bean.Result;
import torna.common.bean.User;
import torna.common.context.UserContext;
import torna.dao.entity.Space;
import torna.service.SpaceService;
import torna.service.dto.SpaceAddDTO;
import torna.service.dto.SpaceInfoDTO;
import torna.service.dto.SpaceDTO;
import torna.web.controller.space.param.SpaceParam;
import torna.web.controller.space.param.SpaceUpdateParam;

import javax.validation.Valid;
import java.util.Collections;
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
        User user = UserContext.getUser();
        Space space = spaceService.getById(param.getId());
        space.setName(param.getName());
        space.setModifierId(user.getUserId());
        space.setModifierName(user.getNickname());
        spaceService.updateIgnoreNull(space);
        return Result.ok();
    }

    @GetMapping("info")
    public Result<SpaceInfoDTO> getSpaceInfo(@HashId Long spaceId) {
        SpaceInfoDTO spaceInfo = spaceService.getSpaceInfo(spaceId);
        return Result.ok(spaceInfo);
    }

    /**
     * 返回用户所在的空间
     * @return
     */
    @GetMapping("list")
    public Result<List<SpaceDTO>> listUserSpace() {
        User user = UserContext.getUser();
        return Result.ok(spaceService.listSpace(user));
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
        space.setCreatorName(user.getNickname());
        if (!user.isSuperAdmin()) {
            space.setAdminIds(Collections.singletonList(user.getUserId()));
        }
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
