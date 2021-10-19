package cn.torna.web.controller.compose;

import cn.torna.common.annotation.HashId;
import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.Result;
import cn.torna.common.enums.ModuleConfigTypeEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.ComposeCommonParam;
import cn.torna.dao.entity.ComposeProject;
import cn.torna.service.ComposeCommonParamService;
import cn.torna.service.ComposeProjectService;
import cn.torna.service.dto.DocParamDTO;
import cn.torna.web.controller.compose.param.ComposeProjectGlobalParam;
import cn.torna.web.controller.compose.param.ComposeProjectSettingParam;
import cn.torna.web.controller.compose.vo.ComposeProjectGlobalParamVO;
import cn.torna.web.controller.compose.vo.ComposeProjectSettingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("compose/project/setting")
public class ComposeProjectSettingController {

    @Autowired
    private ComposeCommonParamService composeCommonParamService;

    @Autowired
    private ComposeProjectService composeProjectService;

    @GetMapping("/get")
    public Result<ComposeProjectSettingVO> getSetting(@HashId Long id) {
        ComposeProject composeProject = composeProjectService.getById(id);
        ComposeProjectSettingVO settingVO = CopyUtil.copyBean(composeProject, ComposeProjectSettingVO::new);
        return Result.ok(settingVO);
    }

    @GetMapping("/getall")
    @NoLogin
    public Result<ComposeProjectSettingVO> getall(@HashId Long id) {
        ComposeProject composeProject = composeProjectService.getById(id);
        ComposeProjectSettingVO settingVO = CopyUtil.copyBean(composeProject, ComposeProjectSettingVO::new);

        List<ComposeCommonParam> docParams = composeCommonParamService.listGlobalParams(id);
        List<ComposeProjectGlobalParamVO> globalParamVOS = CopyUtil.copyList(docParams, ComposeProjectGlobalParamVO::new);


        List<ComposeCommonParam> docReturns = composeCommonParamService.listGlobalReturns(id);
        List<ComposeProjectGlobalParamVO> globalReturnVOS = CopyUtil.copyList(docReturns, ComposeProjectGlobalParamVO::new);

        settingVO.setGlobalParams(globalParamVOS);
        settingVO.setGlobalReturns(globalReturnVOS);

        return Result.ok(settingVO);
    }
    @PostMapping("/save")
    public Result setGatewayUrl(@RequestBody ComposeProjectSettingParam param) {
        ComposeProject composeProject = composeProjectService.getById(param.getId());
        CopyUtil.copyPropertiesIgnoreNull(param, composeProject);
        composeProjectService.update(composeProject);
        return Result.ok();
    }

    // globalParams

    @PostMapping("/globalParams/add")
    public Result addParams(@RequestBody ComposeProjectGlobalParam param) {
        DocParamDTO docParamDTO = CopyUtil.copyBean(param, DocParamDTO::new);
        composeCommonParamService.addGlobal(docParamDTO, param.getProjectId(), ModuleConfigTypeEnum.GLOBAL_PARAMS);
        return Result.ok();
    }

    @GetMapping("/globalParams/list")
    public Result<List<ComposeProjectGlobalParamVO>> listParams(@HashId Long projectId) {
        List<ComposeCommonParam> docParams = composeCommonParamService.listGlobalParams(projectId);
        List<ComposeProjectGlobalParamVO> moduleConfigVOS = CopyUtil.copyList(docParams, ComposeProjectGlobalParamVO::new);
        return Result.ok(moduleConfigVOS);
    }

    @PostMapping("/globalParams/update")
    public Result updateParams(@RequestBody ComposeProjectGlobalParam param) {
        this.updateGlobal(param);
        return Result.ok();
    }

    @PostMapping("/globalParams/delete")
    public Result deleteParams(@RequestBody ComposeProjectGlobalParam param) {
        this.deleteGlobal(param);
        return Result.ok();
    }

    // globalReturns

    @PostMapping("/globalReturns/add")
    public Result addReturns(@RequestBody ComposeProjectGlobalParam param) {
        DocParamDTO docParamDTO = CopyUtil.copyBean(param, DocParamDTO::new);
        composeCommonParamService.addGlobal(docParamDTO, param.getProjectId(), ModuleConfigTypeEnum.GLOBAL_RETURNS);
        return Result.ok();
    }

    @GetMapping("/globalReturns/list")
    public Result<List<ComposeProjectGlobalParamVO>> listReturns(@HashId Long projectId) {
        List<ComposeCommonParam> docParams = composeCommonParamService.listGlobalReturns(projectId);
        List<ComposeProjectGlobalParamVO> moduleConfigVOS = CopyUtil.copyList(docParams, ComposeProjectGlobalParamVO::new);
        return Result.ok(moduleConfigVOS);
    }

    @PostMapping("/globalReturns/update")
    public Result updateReturns(@RequestBody ComposeProjectGlobalParam param) {
        this.updateGlobal(param);
        return Result.ok();
    }

    @PostMapping("/globalReturns/delete")
    public Result deleteReturns(@RequestBody ComposeProjectGlobalParam param) {
        this.deleteGlobal(param);
        return Result.ok();
    }

    private void updateGlobal(ComposeProjectGlobalParam param) {
        ComposeCommonParam commonParam = composeCommonParamService.getById(param.getId());
        CopyUtil.copyPropertiesIgnoreNull(param, commonParam);
        composeCommonParamService.update(commonParam);
    }

    private void deleteGlobal(ComposeProjectGlobalParam param) {
        composeCommonParamService.deleteById(param.getId());
    }
}
