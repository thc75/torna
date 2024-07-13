package cn.torna.service;

import cn.torna.common.util.DataIdUtil;
import cn.torna.dao.entity.ModuleEnvironmentParam;
import cn.torna.dao.mapper.ModuleEnvironmentParamMapper;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * @author tanghc
 */
@Service
public class ModuleEnvironmentParamService extends BaseLambdaService<ModuleEnvironmentParam, ModuleEnvironmentParamMapper> {

    public ModuleEnvironmentParam getByDataId(String dataId) {
        return this.get(ModuleEnvironmentParam::getDataId, dataId);
    }

    public List<ModuleEnvironmentParam> listByEnvironmentAndStyle(Long environmentId, byte style) {
        if (environmentId == null) {
            return Collections.emptyList();
        }
        Query query = this.query()
                .eq(ModuleEnvironmentParam::getEnvironmentId, environmentId)
                .eq(ModuleEnvironmentParam::getStyle, style)
                .orderBy(ModuleEnvironmentParam::getOrderIndex, Sort.ASC)
                .orderBy(ModuleEnvironmentParam::getId, Sort.ASC);
        return list(query);
    }

    /**
     * 获取所有的公共参数
     *
     * @param environmentId 环境id
     * @return
     */
    public List<ModuleEnvironmentParam> listAllByEnvironment(long environmentId) {
        Query query = this.query()
                .eq(ModuleEnvironmentParam::getEnvironmentId, environmentId)
                .orderBy(ModuleEnvironmentParam::getOrderIndex, Sort.ASC)
                .orderBy(ModuleEnvironmentParam::getId, Sort.ASC);
        return list(query);
    }

    @Override
    public int save(ModuleEnvironmentParam entity) {
        initDataId(entity);
        return super.save(entity);
    }

    @Override
    public int saveBatch(Collection<ModuleEnvironmentParam> entityList) {
        for (ModuleEnvironmentParam param : entityList) {
            initDataId(param);
        }
        return super.saveBatch(entityList);
    }

    public static void initDataId(ModuleEnvironmentParam param) {
        String dataId = DataIdUtil.getDocParamDataId(param.getEnvironmentId(), param.getParentId(), param.getStyle(), param.getName());
        param.setDataId(dataId);
    }

    public void deleteByEnvId(long envId) {
        Query query = this.query()
                .eq(ModuleEnvironmentParam::getEnvironmentId, envId);
        this.getMapper().forceDeleteByQuery(query);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteGlobalParam(long id) {
        this.getMapper().forceDeleteById(id);
        // delete children
        this.forceDeleteByQuery(this.query().eq(ModuleEnvironmentParam::getParentId, id));
    }

}
