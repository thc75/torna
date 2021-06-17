package cn.torna.service;

import cn.torna.common.bean.User;
import cn.torna.common.exception.BizException;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 负责项目、模块复制
 * @author tanghc
 */
@Service
public class TransferService {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private ProjectService projectService;


    /**
     * 转移模块
     * @param user 操作人
     * @param moduleId 模块id
     * @param newProjectId 新项目id
     */
    public void transferModule(User user, long moduleId, long newProjectId) {
        Module module = moduleService.getById(moduleId);
        this.checkOperateModule(user, module);
        module.setProjectId(newProjectId);
        moduleService.update(module);
    }



    /**
     * 转移项目
     * @param user 操作人
     * @param projectId 操作项目
     * @param newSpaceId 新空间id
     */
    public void transferProject(User user, long projectId, long newSpaceId) {
        if (!projectService.canOperateProject(user, projectId)) {
            throw new BizException("无权操作");
        }
        Project project = projectService.getById(projectId);
        project.setSpaceId(newSpaceId);
        projectService.update(project);
    }

//    /**
//     * 复制模块
//     * @param user 操作人
//     * @param moduleId 模块id
//     * @param newProjectId 新项目id
//     */
//    public void copyModule(User user, long moduleId, long newProjectId) {
//        Module module = moduleService.getById(moduleId);
//        this.checkOperateModule(user, module);
//        // 创建新的模块
//        Module newModule = CopyUtil.copyBean(module, Module::new);
//        newModule.setProjectId(newProjectId);
//        moduleService.save(newModule);
//        // 复制模块下的文档
//
//        // 复制文档参数
//
//    }


    private void checkOperateModule(User user, Module module) {
        if (!projectService.canOperateProject(user, module.getProjectId())) {
            throw new BizException("无权操作");
        }
    }

}
