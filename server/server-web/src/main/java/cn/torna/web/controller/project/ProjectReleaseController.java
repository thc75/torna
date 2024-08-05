package cn.torna.web.controller.project;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.enums.UserSubscribeTypeEnum;
import cn.torna.service.ProjectReleaseService;
import cn.torna.service.UserSubscribeService;
import cn.torna.service.dto.ProjectReleaseDTO;
import cn.torna.web.config.UserContext;
import cn.torna.web.controller.project.param.ProjectReleaseAddParam;
import cn.torna.web.controller.project.param.ProjectReleaseBindParam;
import cn.torna.web.controller.project.param.ProjectReleaseRemoveParam;
import cn.torna.web.controller.project.param.ProjectReleaseUpdateParam;
import cn.torna.web.controller.user.param.UserSubscribeParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 项目版本管理
 *
 * @author qiuyu
 */
@RestController
@RequestMapping("project/release")
public class ProjectReleaseController {

    @Resource
    private ProjectReleaseService projectReleaseService;
    @Resource
    private UserSubscribeService userSubscribeService;

    /**
     * 分页查询项目版本
     *
     * @author qiuyu
     * @return
     */
    @GetMapping("/list")
    public Result<List<ProjectReleaseDTO>> page(
            @HashId Long projectId,
            @RequestParam(required = false) String releaseNo,
            @RequestParam(required = false) Integer status) {
        User user = UserContext.getUser();
        List<ProjectReleaseDTO> projectUserDTOList = projectReleaseService.pageProjectRelease(
                user.getUserId(),
                projectId,
                releaseNo,
                status
        );
        return Result.ok(projectUserDTOList);
    }

    /**
     * 添加项目版本
     *
     * @author qiuyu
     * @param param 项目版本新增入参
     * @return
     */
    @PostMapping("add")
    public Result add(@RequestBody @Valid ProjectReleaseAddParam param) {
        projectReleaseService.addProjectRelease(
                param.getProjectId(),
                param.getReleaseNo(),
                param.getReleaseDesc(),
                param.getStatus(),
                param.getDingdingWebhook(),
                param.getModuleSourceIdMap()
        );
        return Result.ok();
    }

    /**
     * 修改项目版本
     *
     * @author qiuyu
     * @param param 项目版本修改入参
     * @return
     */
    @PostMapping("update")
    public Result update(@RequestBody @Valid ProjectReleaseUpdateParam param) {
        projectReleaseService.updateProjectRelease(
                param.getId(),
                param.getReleaseDesc(),
                param.getStatus(),
                param.getDingdingWebhook(),
                param.getModuleSourceIdMap()
        );
        return Result.ok();
    }

    /**
     * 修改项目状态
     *
     * @author qiuyu
     * @param param 项目版本状态修改入参
     * @return
     */
    @PostMapping("status")
    public Result updateStatus(@RequestBody @Valid ProjectReleaseUpdateParam param) {
        projectReleaseService.updateStatus(param.getId(), param.getStatus());
        return Result.ok();
    }

    /**
     * 移除用户
     *
     * @author qiuyu
     * @param param 项目版本状删除入参
     */
    @PostMapping("remove")
    public Result remove(@RequestBody @Valid ProjectReleaseRemoveParam param) {
        projectReleaseService.deleteByReleaseId(param.getId());
        return Result.ok();
    }

    /**
     * 查询项目版本绑定文档
     *
     * @author qiuyu
     * @deprecated 切换为新增、修改时绑定
     * @param param 项目版本绑定文档入参
     * @return
     */
    @PostMapping("/bind/list")
    public Result bindList(@RequestBody @Valid ProjectReleaseBindParam param) {
        return Result.ok(projectReleaseService.bindList(param.getProjectId(), param.getReleaseId()));
    }

    /**
     * 订阅版本
     *
     * @author qiuyu
     * @param param param
     */
    @PostMapping("doc/subscribe")
    public Result subscribeDoc(@RequestBody UserSubscribeParam param) {
        User user = UserContext.getUser();
        userSubscribeService.subscribe(user.getUserId(), UserSubscribeTypeEnum.RELEASE, param.getSourceId());
        return Result.ok();
    }

    /**
     * 取消订阅版本
     *
     * @author qiuyu
     * @param param param
     */
    @PostMapping("doc/cancelSubscribe")
    public Result cancelSubscribe(@RequestBody UserSubscribeParam param) {
        User user = UserContext.getUser();
        userSubscribeService.cancelSubscribe(user.getUserId(), UserSubscribeTypeEnum.RELEASE, param.getSourceId());
        return Result.ok();
    }

}
