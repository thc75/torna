package cn.torna.web.controller.doc;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.enums.DebugScriptScopeEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.DebugScript;
import cn.torna.service.DebugScriptService;
import cn.torna.service.DocInfoProService;
import cn.torna.service.dto.DocRefDTO;
import cn.torna.web.controller.doc.param.DebugScriptAddParam;
import cn.torna.web.controller.doc.param.DebugScriptUpdateParam;
import cn.torna.web.controller.doc.vo.DebugScriptVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 调试脚本
 * @author tanghc
 */
@RestController
@RequestMapping("doc/debugscript")
public class DebugScriptController {

    @Autowired
    private DebugScriptService debugScriptService;

    @Autowired
    private DocInfoProService docInfoProService;

    @GetMapping("list")
    public Result<List<DebugScriptVO>> listPre(@HashId Long docId) {
        List<DebugScript> list = debugScriptService.list(docId);
        List<DebugScriptVO> debugScriptVOS = CopyUtil.copyList(list, DebugScriptVO::new);
        return Result.ok(debugScriptVOS);
    }

    @PostMapping("add")
    public Result add(@RequestBody DebugScriptAddParam param) {
        User user = UserContext.getUser();
        DebugScript debugScript = CopyUtil.copyBean(param, DebugScript::new);
        Long docId = param.getDocId();
        Byte scope = param.getScope();
        debugScript.setRefId(getRefId(docId, scope));
        debugScript.setCreatorName(user.getNickname());
        debugScriptService.save(debugScript);
        return Result.ok();
    }

    private long getRefId(long docId, byte scope) {
        long refId = 0L;
        DocRefDTO docRefInfo = docInfoProService.getDocRefInfo(docId);
        switch (DebugScriptScopeEnum.of(scope)) {
            case DOC:
                refId = docRefInfo.getDocId();
                break;
            case MODULE:
                refId = docRefInfo.getModuleId();
                break;
            case PROJECT:
                refId = docRefInfo.getProjectId();
            default:
        }
        return refId;
    }

    @PostMapping("update")
    public Result update(@RequestBody DebugScriptUpdateParam param) {
        DebugScript debugScript = debugScriptService.getById(param.getId());
        CopyUtil.copyPropertiesIgnoreNull(param, debugScript);
        Long docId = param.getDocId();
        Byte scope = param.getScope();
        debugScript.setRefId(getRefId(docId, scope));
        debugScriptService.update(debugScript);
        return Result.ok();
    }

    @PostMapping("delete")
    public Result delete(@RequestBody DebugScriptUpdateParam param) {
        DebugScript debugScript = debugScriptService.getById(param.getId());
        debugScriptService.delete(debugScript);
        return Result.ok();
    }

}
