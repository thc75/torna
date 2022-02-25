package cn.torna.web.controller.space;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.enums.RoleEnum;
import cn.torna.service.SpaceService;
import cn.torna.service.dto.SpaceUserInfoDTO;
import cn.torna.service.dto.UserInfoDTO;
import cn.torna.web.controller.space.param.SpaceMemberAddParam;
import cn.torna.web.controller.space.param.SpaceMemberPageParam;
import cn.torna.web.controller.space.param.SpaceMemberRemoveParam;
import cn.torna.web.controller.space.param.SpaceMemberUpdateParam;
import com.gitee.fastmybatis.core.support.PageEasyui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 空间成员
 * @author tanghc
 */
@RestController
@RequestMapping("space/member")
public class SpaceMemberController {

    @Autowired
    private SpaceService spaceService;

    @GetMapping("/search")
    public Result<List<UserInfoDTO>> search(
            @HashId
            Long spaceId
            , @RequestParam(required = false) String username
    ) {
        List<UserInfoDTO> userInfoDTOS = spaceService.searchSpaceUser(spaceId, username);
        return Result.ok(userInfoDTOS);
    }

    @GetMapping("/all")
    public Result<List<UserInfoDTO>> search(
            @HashId Long spaceId
    ) {
        List<UserInfoDTO> userInfoDTOS = spaceService.listAllSpaceUser(spaceId);
        return Result.ok(userInfoDTOS);
    }

    /**
     * 分页查询空间成员
     * @return
     */
    @PostMapping("/page")
    public Result<PageEasyui<SpaceUserInfoDTO>> page(@Valid @RequestBody SpaceMemberPageParam param) {
        String username = param.getUsername();
        PageEasyui<SpaceUserInfoDTO> pageSpaceUser = spaceService.pageSpaceUser(param.getSpaceId(), username, param);
        return Result.ok(pageSpaceUser);
    }


    /**
     * 添加空间成员
     * @param param
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody @Valid SpaceMemberAddParam param) {
        spaceService.addSpaceUser(param.getSpaceId(), param.getUserIds(), RoleEnum.of(param.getRoleCode()));
        return Result.ok();
    }

    /**
     * 修改成员
     * @param param
     * @return
     */
    @PostMapping("update")
    public Result update(@RequestBody @Valid SpaceMemberUpdateParam param) {
        spaceService.updateSpaceUserRole(
                param.getSpaceId()
                , param.getUserId()
                , RoleEnum.of(param.getRoleCode())
        );
        return Result.ok();
    }

    /**
     * 移除空间成员
     * @param param
     * @return
     */
    @PostMapping("/remove")
    public Result remove(@RequestBody @Valid SpaceMemberRemoveParam param) {
        spaceService.removeMember(param.getSpaceId(), param.getUserId());
        return Result.ok();
    }

}
