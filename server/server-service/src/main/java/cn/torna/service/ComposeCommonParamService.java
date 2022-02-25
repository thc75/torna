package cn.torna.service;


import cn.torna.common.enums.ModuleConfigTypeEnum;
import cn.torna.common.enums.ParamStyleEnum;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.DataIdUtil;
import cn.torna.common.util.IdGen;
import cn.torna.dao.entity.ComposeCommonParam;
import cn.torna.dao.mapper.ComposeCommonParamMapper;
import cn.torna.service.dto.DocParamDTO;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.torna.service.ModuleConfigService.buildStyle;


/**
 * @author tanghc
 */
@Service
public class ComposeCommonParamService extends BaseService<ComposeCommonParam, ComposeCommonParamMapper> {

    public List<ComposeCommonParam> listGlobalParams(long projectId) {
        return this.listGlobal(projectId, ModuleConfigTypeEnum.GLOBAL_PARAMS);
    }

    public List<ComposeCommonParam> listGlobalReturns(long projectId) {
        return this.listGlobal(projectId, ModuleConfigTypeEnum.GLOBAL_RETURNS);
    }

    public List<ComposeCommonParam> listGlobal(long projectId, ModuleConfigTypeEnum moduleConfigTypeEnum) {
        ParamStyleEnum paramStyleEnum = buildStyle(moduleConfigTypeEnum);
        Query query = new Query()
                .eq("compose_project_id", projectId)
                .eq("style", paramStyleEnum.getStyle())
                .orderby("id", Sort.ASC);
        return list(query);
    }

    public void addGlobal(DocParamDTO docParamDTO, long projectId, ModuleConfigTypeEnum moduleConfigTypeEnum) {
        this.saveDocParam(docParamDTO, projectId, moduleConfigTypeEnum);
    }


    public void saveDocParam(DocParamDTO docParamDTO, long projectId, ModuleConfigTypeEnum moduleConfigTypeEnum) {
        if (docParamDTO.getParentId() == null) {
            docParamDTO.setParentId(0L);
        }
        ParamStyleEnum paramStyleEnum = buildStyle(moduleConfigTypeEnum);
        ComposeCommonParam docParam = CopyUtil.copyBean(docParamDTO, ComposeCommonParam::new);
        String dataId = DataIdUtil.getDocParamDataId(IdGen.genId(), docParam.getParentId(), paramStyleEnum.getStyle(), docParam.getName());
        docParam.setDataId(dataId);
        docParam.setStyle(paramStyleEnum.getStyle());
        docParam.setComposeProjectId(projectId);
        save(docParam);
    }

}