package torna.web.controller;

import com.gitee.fastmybatis.core.query.Query;
import torna.common.bean.Result;
import torna.service.SpaceService;
import torna.service.dto.UserInfoDTO;
import torna.web.controller.param.SpaceMemberAddParam;
import torna.web.controller.param.SpaceMemberParam;
import torna.web.controller.param.SpaceMemberRemoveParam;
import com.gitee.fastmybatis.core.support.PageEasyui;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public Result<List<UserInfoDTO>> search(@Valid SpaceMemberParam param) {
        List<UserInfoDTO> userInfoDTOS = spaceService.searchSpaceUser(param.getSpaceId(), param.getUsername());
        return Result.ok(userInfoDTOS);
    }

    /**
     * 分页查询空间成员
     * @param param
     * @return
     */
    @GetMapping("/page")
    public Result<PageEasyui<UserInfoDTO>> page(@Valid SpaceMemberParam param) {
        PageEasyui<UserInfoDTO> pageSpaceUser = spaceService.pageSpaceUser(param.getSpaceId(), param.getUsername());
        return Result.ok(pageSpaceUser);
    }


    /**
     * 添加空间成员
     * @param param
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody @Valid SpaceMemberAddParam param) {
        spaceService.addSpaceUser(param.getSpaceId(), param.getUserIds(), false);
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
