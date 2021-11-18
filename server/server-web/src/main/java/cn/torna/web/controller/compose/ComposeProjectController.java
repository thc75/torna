package cn.torna.web.controller.compose;

import cn.torna.common.annotation.HashId;
import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.enums.ComposeProjectTypeEnum;
import cn.torna.common.enums.StatusEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.ComposeProject;
import cn.torna.service.ComposeProjectService;
import cn.torna.service.dto.ComposeProjectAddDTO;
import cn.torna.service.dto.ComposeProjectUpdateDTO;
import cn.torna.web.controller.compose.param.ComposeProjectCheckPasswordParam;
import cn.torna.web.controller.compose.vo.ComposeProjectVO;
import cn.torna.web.controller.system.param.IdParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("compose/project")
public class ComposeProjectController {

    @Autowired
    private ComposeProjectService composeProjectService;


    /**
     * 获取项目
     * @param id
     * @return
     */
    @GetMapping("get")
    @NoLogin
    public Result<ComposeProjectVO> get(@HashId Long id) {
        ComposeProject composeProject = composeProjectService.getById(id);
        ComposeProjectVO composeProjectVO = CopyUtil.copyBean(composeProject, ComposeProjectVO::new);
        return Result.ok(composeProjectVO);
    }

    /**
     * 添加项目
     * @param projectAddDTO
     * @return
     */
    @PostMapping("add")
    public Result composeAdd(@RequestBody ComposeProjectAddDTO projectAddDTO) {
        User user = UserContext.getUser();
        projectAddDTO.setCreatorId(user.getUserId());
        projectAddDTO.setCreatorName(user.getNickname());
        composeProjectService.addProject(projectAddDTO);
        return Result.ok();
    }

    /**
     * 修改项目
     * @param composeProjectUpdateDTO
     * @return
     */
    @PostMapping("update")
    public Result composeUpdate(@RequestBody ComposeProjectUpdateDTO composeProjectUpdateDTO) {
        composeProjectService.updateProject(composeProjectUpdateDTO);
        return Result.ok();
    }

    /**
     * 删除项目
     * @param param
     * @return
     */
    @PostMapping("delete")
    public Result composeDelete(@RequestBody IdParam param) {
        composeProjectService.deleteById(param.getId());
        return Result.ok();
    }

    @PostMapping("checkPassword")
    @NoLogin
    public Result checkPassword(@RequestBody ComposeProjectCheckPasswordParam param) {
        ComposeProject composeProject = composeProjectService.getById(param.getId());
        this.checkConfig(composeProject);
        if (composeProject.getType() == ComposeProjectTypeEnum.PUBLIC.getType()) {
            return Result.ok();
        }
        String password = composeProject.getPassword();
        if (!Objects.equals(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)), param.getPassword())) {
            throw new BizException("Password Error");
        }
        return Result.ok();
    }

    private void checkConfig(ComposeProject composeProject) {
        if (composeProject == null) {
            throw new BizException("Document not found");
        }
        if (composeProject.getStatus() == StatusEnum.DISABLED.getStatus()) {
            throw new BizException("Url invalid");
        }
    }
    
    
}