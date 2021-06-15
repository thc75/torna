package cn.torna.service;

import cn.torna.common.enums.DebugScriptScopeEnum;
import cn.torna.common.enums.DebugScriptTypeEnum;
import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.DebugScript;
import cn.torna.dao.mapper.DebugScriptMapper;
import cn.torna.service.dto.DocRefDTO;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tanghc
 */
@Service
public class DebugScriptService extends BaseService<DebugScript, DebugScriptMapper> {

    @Autowired
    private DocInfoService docInfoService;

    public List<DebugScript> list(Long docId) {
        DocRefDTO docRefInfo = docInfoService.getDocRefInfo(docId);
        Query query = new Query();
        query.sql("(ref_id=? and scope=?) OR (ref_id=? and scope=?) OR (ref_id=? and scope=?)",
                docRefInfo.getDocId(), DebugScriptScopeEnum.DOC.getScope(),
                docRefInfo.getModuleId(), DebugScriptScopeEnum.MODULE.getScope(),
                docRefInfo.getProjectId(), DebugScriptScopeEnum.PROJECT.getScope()
        );
        return list(query);
    }

}