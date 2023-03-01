package cn.torna.service;

import cn.torna.common.enums.RoleEnum;
import cn.torna.dao.entity.Project;
import cn.torna.service.dto.AllocateProjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author thc
 */
@Service
public class AllocateProjectService {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SpaceService spaceService;


    /**
     * 分配项目
     * @param allocateProjectDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void allocateProject(AllocateProjectDTO allocateProjectDTO) {
        Long userId = allocateProjectDTO.getUserId();
        List<Long> projectIds = allocateProjectDTO.getProjectIds();
        List<Long> spaceIds = projectService.listByCollection("id", projectIds)
                .stream()
                .map(Project::getSpaceId)
                .distinct()
                .collect(Collectors.toList());
        // 先添加到空间
        for (Long spaceId : spaceIds) {
            spaceService.addSpaceUser(
                    spaceId,
                    Collections.singletonList(userId),
                    RoleEnum.of(allocateProjectDTO.getRole())
            );
        }
        // 添加到项目中去
        for (Long projectId : projectIds) {
            projectService.addProjectUser(
                    projectId,
                    Collections.singletonList(userId),
                    RoleEnum.of(allocateProjectDTO.getRole())
            );
        }



    }

}
