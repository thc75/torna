package cn.torna.web.controller.space;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.Space;
import cn.torna.service.SpaceService;
import cn.torna.service.dto.SpaceAddDTO;
import cn.torna.service.dto.SpaceDTO;
import cn.torna.service.dto.SpaceInfoDTO;
import cn.torna.web.controller.space.param.SpaceParam;
import cn.torna.web.controller.space.param.SpaceUpdateParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        spaceService.update(space);
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
     * 返回用户所在的非聚合空间
     * @return
     */
    @GetMapping("listNormal")
    public Result<List<SpaceDTO>> listNormal() {
        User user = UserContext.getUser();
        List<SpaceDTO> spaceDTOS = spaceService.listSpace(user);
        List<SpaceDTO> list = spaceDTOS.stream()
                .filter(spaceDTO -> spaceDTO.getIsCompose() == Booleans.FALSE)
                .collect(Collectors.toList());
        return Result.ok(list);
    }

    /**
     * 添加空间
     * @param spaceAddDTO
     * @return
     */
    @PostMapping("add")
    public Result<SpaceDTO> add(@RequestBody SpaceAddDTO spaceAddDTO) {
        User user = UserContext.getUser();
        spaceAddDTO.setCreatorId(user.getUserId());
        spaceAddDTO.setCreatorName(user.getNickname());
        if (!user.isSuperAdmin()) {
            spaceAddDTO.setAdminIds(Collections.singletonList(user.getUserId()));
        }
        Space space = spaceService.addSpace(spaceAddDTO);
        SpaceDTO spaceDTO = CopyUtil.copyBean(space, SpaceDTO::new);
        return Result.ok(spaceDTO);
    }

    @PostMapping("delete")
    public Result del(@RequestBody SpaceParam param) {
        User user = UserContext.getUser();
        Space space = spaceService.getById(param.getId());
        List<Long> spaceAdminIds = spaceService.listSpaceAdminId(space.getId());
        if (!user.isSuperAdmin() && !spaceAdminIds.contains(user.getUserId())) {
            throw new BizException("无操作权限");
        }
        space.setModifierId(user.getUserId());
        space.setModifierName(user.getNickname());
        space.setIsDeleted(Booleans.TRUE);
        spaceService.update(space);
        return Result.ok();
    }

}
