package cn.torna.web.controller.third;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.service.metersphere.MeterSphereService;
import cn.torna.service.metersphere.MeterSphereVersion;
import cn.torna.service.metersphere.dto.MeterSphereSpaceConfigSaveDTO;
import cn.torna.service.metersphere.dto.MeterSphereModuleDTO;
import cn.torna.service.metersphere.dto.MeterSphereModuleConfigSaveDTO;
import cn.torna.service.metersphere.dto.MeterSphereProjectDTO;
import cn.torna.service.metersphere.dto.MeterSphereSetting;
import cn.torna.service.metersphere.dto.MeterSphereTestDTO;
import cn.torna.web.controller.third.param.MeterSphereConfigSaveParam;
import cn.torna.web.controller.third.param.MeterSphereModuleSaveParam;
import cn.torna.web.controller.third.vo.MeterSphereProjectVO;
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
 * @author thc
 */
@RestController
@RequestMapping("third/metersphere")
public class MeterSphereController {

    @Autowired
    private MeterSphereService meterSphereService;

    @GetMapping("/space/test")
    public Result<MeterSphereTestDTO> test(MeterSphereSetting meterSphereSetting) {
        Integer version = meterSphereSetting.getVersion();
        if (version == null || Objects.equals(version, MeterSphereVersion.V2)) {
            MeterSphereTestDTO meterSphereTestDTO = MeterSphereService.test(meterSphereSetting);
            return Result.ok(meterSphereTestDTO);
        } else if (Objects.equals(version, MeterSphereVersion.V3)) {
            MeterSphereTestDTO meterSphereTestDTO = MeterSphereService.testV3(meterSphereSetting);
            return Result.ok(meterSphereTestDTO);
        } else {
            throw new BizException("不支持版本：" + version);
        }
    }

    @PostMapping("/space/save")
    public Result<?> save(@Valid @RequestBody MeterSphereConfigSaveParam param) {
        meterSphereService.save(CopyUtil.copyBean(param, MeterSphereSpaceConfigSaveDTO::new));
        return Result.ok();
    }

    @GetMapping("/space/load")
    public Result<MeterSphereSpaceConfigSaveDTO> load(@HashId Long spaceId) {
        MeterSphereSpaceConfigSaveDTO sphereConfigSaveDTO = meterSphereService.getSpaceConfig(spaceId);
        return Result.ok(sphereConfigSaveDTO);
    }

    @GetMapping("/project/load")
    public Result<MeterSphereProjectVO> loadSpaceProject(@HashId Long projectId) {
        MeterSphereProjectVO meterSphereProjectVO = new MeterSphereProjectVO();
        List<MeterSphereProjectDTO> meterSphereProjectDTOS;
        try {
            meterSphereProjectDTOS = meterSphereService.listProject(projectId);
            meterSphereProjectVO.setProjects(meterSphereProjectDTOS);
        } catch (MeterSphereService.SpaceNotConfigException e) {
            meterSphereProjectVO.setSpaceConfig(false);
            meterSphereProjectVO.setSpaceId(e.getSpaceId());
        }
        return Result.ok(meterSphereProjectVO);
    }

    @GetMapping("/module/list")
    public Result<List<MeterSphereModuleDTO>> loadProjectModule(@HashId Long projectId, String msProjectId) {
        List<MeterSphereModuleDTO> meterSphereModuleDTOS = meterSphereService.listModules(projectId, msProjectId);
        return Result.ok(meterSphereModuleDTOS);
    }

    @GetMapping("/module/load")
    public Result<MeterSphereModuleConfigSaveDTO> loadModule(@HashId Long moduleId) {
        MeterSphereModuleConfigSaveDTO moduleConfig = meterSphereService.getModuleConfig(moduleId);
        return Result.ok(moduleConfig);
    }


    @PostMapping("/module/save")
    public Result<?> saveModule(@Valid @RequestBody MeterSphereModuleSaveParam param) {
        meterSphereService.saveModule(CopyUtil.copyBean(param, MeterSphereModuleConfigSaveDTO::new));
        return Result.ok();
    }

}
