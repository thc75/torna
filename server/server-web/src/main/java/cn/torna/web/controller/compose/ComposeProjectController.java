package cn.torna.web.controller.compose;

import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.ComposeProject;
import cn.torna.service.ComposeProjectService;
import cn.torna.service.dto.ComposeProjectAddDTO;
import cn.torna.service.dto.ComposeProjectUpdateDTO;
import cn.torna.web.controller.system.param.IdParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("compose/project")
public class ComposeProjectController {

    @Autowired
    private ComposeProjectService composeProjectService;

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
     
    
    
}