package cn.torna.web.controller.module;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.DataIdUtil;
import cn.torna.dao.entity.ModuleEnvironmentParam;
import cn.torna.service.ModuleEnvironmentParamService;
import cn.torna.web.controller.module.param.ModuleEnvironmentParamParam;
import cn.torna.web.controller.module.vo.ModuleEnvironmentParamVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * 模块配置-参数
 * @author thc
 */
@RestController
@RequestMapping("module/environment/param")
public class ModuleEnvironmentParamController {

    @Autowired
    private ModuleEnvironmentParamService moduleEnvironmentParamService;

    @PostMapping("add")
    public Result add(@RequestBody ModuleEnvironmentParamParam param) {
        User user = UserContext.getUser();
        String dataId = DataIdUtil.getDocParamDataId(param.getEnvironmentId(), param.getParentId(), param.getStyle(), param.getName());
        ModuleEnvironmentParam record = moduleEnvironmentParamService.getByDataId(dataId);
        if (record != null) {
            throw new BizException(param.getName() + "已存在");
        }
        if (param.getRequired() == null) {
            param.setRequired(Booleans.TRUE);
        }
        ModuleEnvironmentParam moduleEnvironmentParam = CopyUtil.copyBean(param, ModuleEnvironmentParam::new);
        moduleEnvironmentParam.setCreatorId(user.getUserId());
        moduleEnvironmentParam.setCreatorName(user.getNickname());
        moduleEnvironmentParam.setModifierId(user.getUserId());
        moduleEnvironmentParam.setModifierName(user.getNickname());
        moduleEnvironmentParam.setDataId(dataId);
        moduleEnvironmentParamService.save(moduleEnvironmentParam);
        return Result.ok();
    }

    @GetMapping("list")
    public Result<List<ModuleEnvironmentParamVO>> list(@HashId Long environmentId, byte style) {
        List<ModuleEnvironmentParam> list = moduleEnvironmentParamService.listByEnvironmentAndStyle(environmentId, style);
        List<ModuleEnvironmentParamVO> ret = CopyUtil.copyList(list, ModuleEnvironmentParamVO::new);
        return Result.ok(ret);
    }

    @PostMapping("update")
    public Result update(@RequestBody @Valid ModuleEnvironmentParamParam param) {
        User user = UserContext.getUser();
        String dataId = DataIdUtil.getDocParamDataId(param.getEnvironmentId(), param.getParentId(), param.getStyle(), param.getName());
        ModuleEnvironmentParam record = moduleEnvironmentParamService.getByDataId(dataId);
        if (record != null && !Objects.equals(param.getId(), record.getId())) {
            throw new BizException(param.getName() + "已存在");
        }
        ModuleEnvironmentParam moduleEnvironmentParam = moduleEnvironmentParamService.getById(param.getId());
        CopyUtil.copyPropertiesIgnoreNull(param, moduleEnvironmentParam);
        DataIdUtil.getDocParamDataId(param.getEnvironmentId(), param.getParentId(), moduleEnvironmentParam.getStyle(), moduleEnvironmentParam.getName());
        moduleEnvironmentParam.setModifierId(user.getUserId());
        moduleEnvironmentParam.setModifierName(user.getNickname());
        moduleEnvironmentParam.setDataId(dataId);
        moduleEnvironmentParamService.update(moduleEnvironmentParam);
        return Result.ok();
    }

    @PostMapping("delete")
    public Result delete(@RequestBody ModuleEnvironmentParamParam param) {
        moduleEnvironmentParamService.deleteGlobalParam(param.getId());
        return Result.ok();
    }


}
