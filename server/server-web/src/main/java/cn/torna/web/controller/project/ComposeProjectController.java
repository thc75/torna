package cn.torna.web.controller.project;

import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.service.ComposeProjectService;
import cn.torna.service.dto.ComposeProjectAddDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("project/compose")
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
     
    
    
}