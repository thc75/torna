package cn.torna.service.metersphere;

import cn.torna.common.bean.Booleans;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.MsModuleConfig;
import cn.torna.dao.entity.MsSpaceConfig;
import cn.torna.dao.entity.Project;
import cn.torna.service.ProjectService;
import cn.torna.service.metersphere.dto.MeterSphereModuleConfigSaveDTO;
import cn.torna.service.metersphere.dto.MeterSphereModuleDTO;
import cn.torna.service.metersphere.dto.MeterSphereProjectDTO;
import cn.torna.service.metersphere.dto.MeterSphereSetting;
import cn.torna.service.metersphere.dto.MeterSphereSpaceConfigSaveDTO;
import cn.torna.service.metersphere.dto.MeterSphereSpaceDTO;
import cn.torna.service.metersphere.dto.MeterSphereTestDTO;
import cn.torna.service.metersphere.v3.state.AppSettingState;
import cn.torna.service.metersphere.v3.state.MSModule;
import cn.torna.service.metersphere.v3.state.MSOrganization;
import cn.torna.service.metersphere.v3.state.MSProject;
import cn.torna.service.metersphere.v3.util.MSClientUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * @author thc
 */
@Service
@Slf4j
public class MeterSphereService {

    @Autowired
    private MsSpaceConfigService msSpaceConfigService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MsModuleConfigService moduleConfigService;

    public static MeterSphereTestDTO test(MeterSphereSetting meterSphereSetting) {
        try {
            boolean test = MSApiUtil.test(meterSphereSetting);
            if (!test) {
                throw new RuntimeException("请检查配置");
            }
            // 链接成功后获取空间列表
            JSONObject workspaceObj = MSApiUtil.getWorkSpaceList(meterSphereSetting);
            JSONArray data = workspaceObj.getJSONArray("data");
            List<MeterSphereSpaceDTO> spaceDTOS = null;
            if (data != null) {
                spaceDTOS = data.toJavaList(MeterSphereSpaceDTO.class);
            }
            MeterSphereTestDTO meterSphereTestDTO = new MeterSphereTestDTO();
            meterSphereTestDTO.setSuccess(true);
            meterSphereTestDTO.setSpaces(spaceDTOS);
            return meterSphereTestDTO;
        } catch (Exception e) {
            log.error("链接MeterSphere失败, setting={}", meterSphereSetting, e);
            throw new BizException("链接失败：" + e.getMessage());
        }
    }

    public static MeterSphereTestDTO testV3(MeterSphereSetting meterSphereSetting) {
        AppSettingState appSettingState = meterSphereSetting.toAppSettingState();
        try {
            boolean test = MSClientUtils.test(appSettingState);
            if (!test) {
                throw new RuntimeException("请检查配置");
            }
            // 链接成功后获取空间列表
            List<MSOrganization> organizations = MSClientUtils.getOrganizationList(appSettingState);
            organizations = CollectionUtils.isNotEmpty(organizations) ? organizations : getDefaultOrganization();
            List<MeterSphereSpaceDTO> spaceDTOS = CopyUtil.copyList(organizations, MeterSphereSpaceDTO::new);
            MeterSphereTestDTO meterSphereTestDTO = new MeterSphereTestDTO();
            meterSphereTestDTO.setSuccess(true);
            meterSphereTestDTO.setSpaces(spaceDTOS);
            return meterSphereTestDTO;
        } catch (Exception e) {
            log.error("链接MeterSphere失败, setting={}", meterSphereSetting, e);
            throw new BizException("链接失败：" + e.getMessage());
        }
    }

    private static List<MSOrganization> getDefaultOrganization() {
        return Collections.singletonList(new MSOrganization("默认组织", "100001"));
    }


    public void save(MeterSphereSpaceConfigSaveDTO param) {
        MsSpaceConfig msSpaceConfig = msSpaceConfigService.getBySpaceId(param.getSpaceId());
        boolean save = false;
        if (msSpaceConfig == null) {
            save = true;
            msSpaceConfig = new MsSpaceConfig();
        }
        msSpaceConfig.setMsAccessKey(param.getMsAccessKey());
        msSpaceConfig.setMsAddress(param.getMsAddress());
        msSpaceConfig.setMsSecretKey(param.getMsSecretKey());
        msSpaceConfig.setMsSpaceId(param.getMsSpaceId());
        msSpaceConfig.setSpaceId(param.getSpaceId());
        msSpaceConfig.setMsSpaceName(param.getMsSpaceName());
        msSpaceConfig.setVersion(param.getVersion());
        if (save) {
            msSpaceConfigService.save(msSpaceConfig);
        } else {
            msSpaceConfigService.update(msSpaceConfig);
        }
    }

    public MeterSphereSpaceConfigSaveDTO getSpaceConfig(Long spaceId) {
        MsSpaceConfig msSpaceConfig = msSpaceConfigService.getBySpaceId(spaceId);
        return CopyUtil.copyBeanNullable(msSpaceConfig, MeterSphereSpaceConfigSaveDTO::new);
    }

    public MeterSphereModuleConfigSaveDTO getModuleConfig(Long moduleId) {
        MsModuleConfig msModuleConfig = moduleConfigService.getByModuleId(moduleId);
        return CopyUtil.copyBeanNullable(msModuleConfig, MeterSphereModuleConfigSaveDTO::new);
    }


    public List<MeterSphereProjectDTO> listProject(Long projectId) throws SpaceNotConfigException {
        Project project = projectService.getById(projectId);
        Long spaceId = project.getSpaceId();
        MsSpaceConfig msSpaceConfig = msSpaceConfigService.getBySpaceId(spaceId);
        if (msSpaceConfig == null) {
            throw new SpaceNotConfigException(spaceId);
        }
        MeterSphereSetting meterSphereSetting = CopyUtil.copyBean(msSpaceConfig, MeterSphereSetting::new);
        String msSpaceId = msSpaceConfig.getMsSpaceId();
        Integer version = msSpaceConfig.getVersion();
        try {
            switch (version) {
                case MeterSphereVersion.V2:
                    JSONObject projectListJson = MSApiUtil.getProjectList(meterSphereSetting, msSpaceId);
                    JSONArray data = projectListJson.getJSONArray("data");
                    if (data != null) {
                        return data.toJavaList(MeterSphereProjectDTO.class);
                    }
                    break;
                case MeterSphereVersion.V3:
                    AppSettingState appSettingState = meterSphereSetting.toAppSettingState();
                    List<MSProject> projectList = MSClientUtils.getProjectList(appSettingState, msSpaceId);
                    return CopyUtil.copyList(projectList, MeterSphereProjectDTO::new);
                default: {
                    return Collections.emptyList();
                }
            }
        } catch (Exception e) {
            log.error("获取MeterSphere项目失败, setting={}", meterSphereSetting, e);
            throw new BizException("获取MeterSphere项目失败:" + e.getMessage());
        }

        return Collections.emptyList();
    }

    public List<MeterSphereModuleDTO> listModules(Long projectId, String msProjectId) {
        Project project = projectService.getById(projectId);
        Long spaceId = project.getSpaceId();
        MsSpaceConfig msSpaceConfig = msSpaceConfigService.getBySpaceId(spaceId);
        if (msSpaceConfig == null) {
            return Collections.emptyList();
        }
        MeterSphereSetting meterSphereSetting = CopyUtil.copyBean(msSpaceConfig, MeterSphereSetting::new);
        Integer version = msSpaceConfig.getVersion();
        try {
            switch (version) {
                case MeterSphereVersion.V2:
                    JSONObject moduleObj = MSApiUtil.getModuleList(meterSphereSetting, msProjectId, "http");
                    JSONArray data = moduleObj.getJSONArray("data");
                    if (data != null) {
                        return data.toJavaList(MeterSphereModuleDTO.class);
                    }
                    break;
                case MeterSphereVersion.V3:
                    AppSettingState appSettingState = meterSphereSetting.toAppSettingState();
                    List<MSModule> moduleList = MSClientUtils.getModuleList(appSettingState, msProjectId);
                    return CopyUtil.copyList(moduleList, MeterSphereModuleDTO::new);
                default: {
                    return Collections.emptyList();
                }
            }
        } catch (Exception e) {
            log.error("获取MeterSphere项目失败, setting={}", meterSphereSetting, e);
            throw new BizException("获取MeterSphere项目失败:" + e.getMessage());
        }
        return Collections.emptyList();
    }

    public void saveModule(MeterSphereModuleConfigSaveDTO dto) {
        Long moduleId = dto.getModuleId();
        MsModuleConfig msModuleConfig = moduleConfigService.getByModuleId(moduleId);
        boolean save = false;
        if (msModuleConfig == null) {
            save = true;
            msModuleConfig = new MsModuleConfig();
        }
        msModuleConfig.setModuleId(moduleId);
        msModuleConfig.setMsCoverModule(Booleans.TRUE);
        msModuleConfig.setMsModuleId(dto.getMsModuleId());
        msModuleConfig.setMsProjectId(dto.getMsProjectId());
        msModuleConfig.setName(dto.getMsProjectName() + "/" + dto.getMsModuleName());
        if (save) {
            moduleConfigService.save(msModuleConfig);
        } else {
            moduleConfigService.update(msModuleConfig);
        }

    }

    @Getter
    @AllArgsConstructor
    public static class SpaceNotConfigException extends Exception {
        private Long spaceId;
    }
}
