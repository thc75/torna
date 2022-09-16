package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.User;
import cn.torna.common.enums.ModuleTypeEnum;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.GenerateUtil;
import cn.torna.dao.entity.Module;
import cn.torna.dao.mapper.ModuleMapper;
import cn.torna.service.dto.ImportPostmanDTO;
import cn.torna.service.dto.ImportSwaggerDTO;
import cn.torna.service.dto.ImportSwaggerV2DTO;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author tanghc
 */
@Service
public class ModuleService extends BaseService<Module, ModuleMapper> {

    public List<Module> listProjectModules(long projectId) {
        return list("project_id", projectId);
    }

    public Module addModule(String name, long projectId, User user) {
        Module module = new Module();
        module.setType(ModuleTypeEnum.CUSTOM_ADD.getType());
        module.setName(name);
        module.setProjectId(projectId);
        module.setCreateMode(user.getOperationModel());
        module.setCreatorId(user.getUserId());
        module.setModifierId(user.getUserId());
        module.setToken(createToken());
        save(module);
        return module;
    }

    public Module createPostmanModule(ImportPostmanDTO importPostmanDTO, String name) {
        Assert.notNull(name, () -> "name不能为空");
        User user = importPostmanDTO.getUser();
        Long projectId = importPostmanDTO.getProjectId();
        Module module = getByProjectIdAndName(projectId, name);
        if (module == null) {
            module = new Module();
            module.setName(name);
            module.setProjectId(projectId);
            module.setType(ModuleTypeEnum.POSTMAN_IMPORT.getType());
            module.setToken(createToken());
            module.setCreateMode(user.getOperationModel());
            module.setModifyMode(user.getOperationModel());
            module.setCreatorId(user.getUserId());
            module.setModifierId(user.getUserId());
            this.save(module);
        } else {
            module.setType(ModuleTypeEnum.POSTMAN_IMPORT.getType());
            module.setModifyMode(user.getOperationModel());
            module.setModifierId(user.getUserId());
            module.setIsDeleted(Booleans.FALSE);
            this.update(module);
        }
        return module;
    }

    /**
     * 创建swagger导入模块
     * @param importSwaggerDTO 导入配置
     * @param name 模块名称
     * @return 返回模块对象
     */
    public Module createSwaggerModule(ImportSwaggerDTO importSwaggerDTO, String name) {
        Assert.notNull(name, () -> "name不能为空");
        User user = importSwaggerDTO.getUser();
        Long projectId = importSwaggerDTO.getProjectId();
        Module module = getByProjectIdAndName(projectId, name);
        if (module == null) {
            module = new Module();
            module.setName(name);
            module.setProjectId(projectId);
            module.setType(ModuleTypeEnum.SWAGGER_IMPORT.getType());
            module.setImportUrl(importSwaggerDTO.getImportUrl());
            module.setBasicAuthUsername(importSwaggerDTO.getBasicAuthUsername());
            module.setBasicAuthPassword(importSwaggerDTO.getBasicAuthPassword());
            module.setToken(createToken());
            module.setCreateMode(user.getOperationModel());
            module.setModifyMode(user.getOperationModel());
            module.setCreatorId(user.getUserId());
            module.setModifierId(user.getUserId());
            this.save(module);
        } else {
            module.setType(ModuleTypeEnum.SWAGGER_IMPORT.getType());
            module.setImportUrl(importSwaggerDTO.getImportUrl());
            module.setBasicAuthUsername(importSwaggerDTO.getBasicAuthUsername());
            module.setBasicAuthPassword(importSwaggerDTO.getBasicAuthPassword());
            module.setModifyMode(user.getOperationModel());
            module.setModifierId(user.getUserId());
            module.setIsDeleted(Booleans.FALSE);
            this.update(module);
        }
        return module;
    }

    /**
     * 创建swagger导入模块
     * @param importSwaggerDTO 导入配置
     * @param name 模块名称
     * @return 返回模块对象
     */
    public Module createSwaggerModule(ImportSwaggerV2DTO importSwaggerDTO, String name) {
        Assert.notNull(name, () -> "name不能为空");
        User user = importSwaggerDTO.getUser();
        Long projectId = importSwaggerDTO.getProjectId();
        Module module = getByProjectIdAndName(projectId, name);
        if (module == null) {
            module = new Module();
            module.setName(name);
            module.setProjectId(projectId);
            module.setType(ModuleTypeEnum.SWAGGER_IMPORT.getType());
            module.setToken(createToken());
            module.setCreateMode(user.getOperationModel());
            module.setModifyMode(user.getOperationModel());
            module.setCreatorId(user.getUserId());
            module.setModifierId(user.getUserId());
            this.save(module);
        } else {
            module.setType(ModuleTypeEnum.SWAGGER_IMPORT.getType());
            module.setModifyMode(user.getOperationModel());
            module.setModifierId(user.getUserId());
            module.setIsDeleted(Booleans.FALSE);
            this.update(module);
        }
        return module;
    }

    public Module getByToken(String token) {
        Assert.notNull(token, () -> "token不能为null");
        return this.get("token", token);
    }

    public static String createToken() {
        return GenerateUtil.getUUID();
    }

    public Module getByProjectIdAndName(long projectId, String name) {
        Query query = new Query()
                .eq("project_id", projectId)
                .eq("name", name);
        return get(query);
    }

    public void delete(long moduleId, User user) {
        Module module = getById(moduleId);
        module.setModifierId(user.getUserId());
        this.delete(module);
    }

}