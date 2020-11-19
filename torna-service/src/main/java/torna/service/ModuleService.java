package torna.service;

import com.gitee.fastmybatis.core.query.Query;
import org.springframework.util.Assert;
import torna.common.bean.User;
import torna.common.context.UserContext;
import torna.common.enums.ModuleTypeEnum;
import torna.common.support.BaseService;
import torna.dao.entity.Module;
import torna.dao.mapper.ModuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import torna.service.dto.ImportSwaggerDTO;

import java.util.List;

/**
 * @author tanghc
 */
@Service
public class ModuleService extends BaseService<Module, ModuleMapper> {

    /**
     * 创建swagger导入模块
     * @param importSwaggerDTO 导入配置
     * @param name 模块名称
     * @return 返回模块对象
     */
    public Module createSwaggerModule(ImportSwaggerDTO importSwaggerDTO, String name) {
        Assert.notNull(name, () -> "name不能为空");
        User user = UserContext.getUser();
        Long projectId = importSwaggerDTO.getProjectId();
        Module module = getByProjectIdAndName(projectId, name);
        if (module == null) {
            module = new Module();
            module.setName(name);
            module.setProjectId(projectId);
            module.setType(ModuleTypeEnum.SWAGGER_IMPORT.getType());
            module.setImportUrl(importSwaggerDTO.getUrl());
            module.setBasicAuthUsername(importSwaggerDTO.getBasicAuthUsername());
            module.setBasicAuthPassword(importSwaggerDTO.getBasicAuthPassword());
            module.setCreatorId(user.getUserId());
            module.setModifierId(user.getUserId());
            this.saveIgnoreNull(module);
        } else {
            module.setType(ModuleTypeEnum.SWAGGER_IMPORT.getType());
            module.setImportUrl(importSwaggerDTO.getUrl());
            module.setBasicAuthUsername(importSwaggerDTO.getBasicAuthUsername());
            module.setBasicAuthPassword(importSwaggerDTO.getBasicAuthPassword());
            module.setModifierId(user.getUserId());
            this.updateIgnoreNull(module);
        }
        return module;
    }

    public Module getByProjectIdAndName(long projectId, String name) {
        Query query = new Query()
                .eq("project_id", projectId)
                .eq("name", name);
        return get(query);
    }

}