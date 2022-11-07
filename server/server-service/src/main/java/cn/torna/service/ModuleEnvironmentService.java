package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.enums.ParamStyleEnum;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.TreeUtil;
import cn.torna.dao.entity.ModuleEnvironment;
import cn.torna.dao.entity.ModuleEnvironmentParam;
import cn.torna.dao.mapper.ModuleEnvironmentMapper;
import cn.torna.service.dto.ModuleEnvironmentCopyDTO;
import cn.torna.service.dto.ModuleEnvironmentDTO;
import cn.torna.service.dto.ModuleEnvironmentImportDTO;
import cn.torna.service.dto.ModuleEnvironmentParamDTO;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author tanghc
 */
@Service
public class ModuleEnvironmentService extends BaseService<ModuleEnvironment, ModuleEnvironmentMapper> {

    @Autowired
    private ModuleEnvironmentParamService moduleEnvironmentParamService;


    public ModuleEnvironment getFirst(long moduleId) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .orderby("id", Sort.ASC);
        return this.get(query);
    }


    /**
     * 设置调试环境
     */
    public ModuleEnvironment setEnvironment(ModuleEnvironmentDTO moduleEnvironmentSettingDTO) {
        Long moduleId = moduleEnvironmentSettingDTO.getModuleId();
        String name = moduleEnvironmentSettingDTO.getName();
        String url = moduleEnvironmentSettingDTO.getUrl();
        Byte isPublic = moduleEnvironmentSettingDTO.getIsPublic();
        ModuleEnvironment moduleEnvironment = getByModuleIdAndName(moduleId, name);
        if (moduleEnvironment == null) {
            moduleEnvironment = new ModuleEnvironment();
            moduleEnvironment.setModuleId(moduleId);
            moduleEnvironment.setName(name);
            moduleEnvironment.setUrl(url);
            moduleEnvironment.setIsPublic(isPublic);
            save(moduleEnvironment);
        } else {
            moduleEnvironment.setUrl(url);
            moduleEnvironment.setIsPublic(isPublic);
            update(moduleEnvironment);
        }
        return moduleEnvironment;
    }

    public ModuleEnvironment getByModuleIdAndName(long moduleId, String name) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("name", name);
        return get(query);
    }

    /**
     * 设置调试环境
     * @param moduleId 模块id
     * @param name 名称
     * @param url url
     * @param isPublic 是否公开
     */
    public ModuleEnvironment setDebugEnv(long moduleId, String name, String url, boolean isPublic) {
        ModuleEnvironmentDTO moduleEnvironmentSettingDTO = new ModuleEnvironmentDTO();
        moduleEnvironmentSettingDTO.setModuleId(moduleId);
        moduleEnvironmentSettingDTO.setName(name);
        moduleEnvironmentSettingDTO.setUrl(url);
        moduleEnvironmentSettingDTO.setIsPublic(Booleans.toValue(isPublic));
        return setEnvironment(moduleEnvironmentSettingDTO);
    }

    /**
     * 设置模块调试环境
     *
     * @param moduleId 模块id
     * @param name     环境名称
     * @param url      调试路径
     */
    public void setDebugEnv(long moduleId, String name, String url) {
        this.setDebugEnv(moduleId, name, url, false);
    }

    /**
     * 查询模块对应的环境
     * @param moduleId 模块id
     * @return 返回环境
     */
    public List<ModuleEnvironment> listModuleEnvironment(long moduleId) {
        Query query = new Query()
                .eq("module_id", moduleId);
        List<ModuleEnvironment> list = list(query);
        list.sort(Comparator.comparing(ModuleEnvironment::getGmtCreate));
        return list;
    }

    /**
     * 删除模块调试环境
     *
     * @param moduleId 模块id
     * @param name     环境名称
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDebugEnv(long moduleId, String name) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("name", name);
        ModuleEnvironment moduleEnvironment = this.get(query);
        if (moduleEnvironment != null) {
            this.delete(moduleEnvironment);
            moduleEnvironmentParamService.deleteByEnvId(moduleEnvironment.getId());
        }
    }

    /**
     * 拷贝环境
     */
    @Transactional(rollbackFor = Exception.class)
    public void copyEnvironment(ModuleEnvironmentCopyDTO moduleEnvironmentCopyDTO) {
        // 更新环境配置
        Long fromEnvId = moduleEnvironmentCopyDTO.getFromEnvId();
        ModuleEnvironment moduleEnvironmentNew = CopyUtil.copyBean(moduleEnvironmentCopyDTO, ModuleEnvironment::new);
        moduleEnvironmentNew.setModuleId(moduleEnvironmentCopyDTO.getDestModuleId());
        this.save(moduleEnvironmentNew);
        // 新环境id
        Long newEnvId = moduleEnvironmentNew.getId();

        // 更新公共部分
        copyGlobal(fromEnvId, newEnvId, ParamStyleEnum.HEADER);
        copyGlobal(fromEnvId, newEnvId, ParamStyleEnum.REQUEST);
        copyGlobal(fromEnvId, newEnvId, ParamStyleEnum.RESPONSE);
    }

    private void copyGlobal(long fromEnvId, long newEnvId, ParamStyleEnum paramStyleEnum) {
        List<ModuleEnvironmentParam> params = moduleEnvironmentParamService.listByEnvironmentAndStyle(fromEnvId, paramStyleEnum.getStyle());
        List<ModuleEnvironmentParamDTO> fromParams = CopyUtil.copyList(params, ModuleEnvironmentParamDTO::new);
        List<ModuleEnvironmentParamDTO> tree = TreeUtil.convertTree(fromParams, 0L);
        this.doCopy(tree, newEnvId, 0L);
    }

    public void doCopy(List<ModuleEnvironmentParamDTO> tree, long newEnvId, long parentId) {
        for (ModuleEnvironmentParamDTO moduleEnvironmentParamDTO : tree) {
            ModuleEnvironmentParam newParam = CopyUtil.copyBean(moduleEnvironmentParamDTO, ModuleEnvironmentParam::new);
            newParam.setParentId(parentId);
            newParam.setEnvironmentId(newEnvId);
            ModuleEnvironmentParamService.initDataId(newParam);
            moduleEnvironmentParamService.save(newParam);
            List<ModuleEnvironmentParamDTO> children = moduleEnvironmentParamDTO.getChildren();
            // if it has children loop do it.
            if (!CollectionUtils.isEmpty(children)) {
                doCopy(children, newEnvId, newParam.getId());
            }
        }
    }

    /**
     * 导入环境
     * @param moduleEnvironmentImportDTO 导入参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void importEnvironment(ModuleEnvironmentImportDTO moduleEnvironmentImportDTO) {
        Long destModuleId = moduleEnvironmentImportDTO.getDestModuleId();
        Map<Long, ModuleEnvironment> envIdMap = this.listByCollection("id", moduleEnvironmentImportDTO.getEnvIds())
                .stream()
                .collect(Collectors.toMap(ModuleEnvironment::getId, Function.identity()));
        moduleEnvironmentImportDTO.getEnvIds()
                .stream().map(envId -> {
            ModuleEnvironmentCopyDTO moduleEnvironmentCopyDTO = new ModuleEnvironmentCopyDTO();
            ModuleEnvironment otherModuleEnvironment = envIdMap.get(envId);
            moduleEnvironmentCopyDTO.setFromEnvId(envId);
            moduleEnvironmentCopyDTO.setDestModuleId(destModuleId);
            moduleEnvironmentCopyDTO.setName(otherModuleEnvironment.getName());
            moduleEnvironmentCopyDTO.setUrl(otherModuleEnvironment.getUrl());
            moduleEnvironmentCopyDTO.setIsPublic(otherModuleEnvironment.getIsPublic());
            return moduleEnvironmentCopyDTO;
        })
                // 拷贝环境
                .forEach(this::copyEnvironment);
    }

}