package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.ModuleEnvironmentParam;
import cn.torna.dao.mapper.ModuleEnvironmentParamMapper;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author tanghc
 */
@Service
public class ModuleEnvironmentParamService extends BaseService<ModuleEnvironmentParam, ModuleEnvironmentParamMapper> {

    public ModuleEnvironmentParam getByDataId(String dataId) {
        return this.get("data_id", dataId);
    }

    public List<ModuleEnvironmentParam> listByEnvironmentAndStyle(long environmentId, byte style) {
        Query query = new Query().eq("environment_id", environmentId)
                .eq("style", style)
                .orderby("order_index", Sort.ASC)
                .orderby("id", Sort.ASC);
        return list(query);
    }

}