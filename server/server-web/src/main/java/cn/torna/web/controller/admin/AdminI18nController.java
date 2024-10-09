package cn.torna.web.controller.admin;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.bean.Result;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.SystemI18nConfig;
import cn.torna.service.SystemConfigService;
import cn.torna.service.SystemI18nConfigService;
import cn.torna.web.controller.admin.param.SystemI18nConfigParam;
import cn.torna.web.controller.admin.vo.SystemI18nVO;
import cn.torna.web.controller.doc.vo.IdVO;
import cn.torna.web.controller.system.param.IdParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 国际化配置
 * @author thc
 */
@RestController
@RequestMapping("admin/i18n")
public class AdminI18nController {

    @Autowired
    private SystemI18nConfigService systemI18nConfigService;

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping("get")
    public Result<SystemI18nVO> getLangContent(@HashId Long id) {
        SystemI18nConfig systemI18nConfig = systemI18nConfigService.getById(id);
        SystemI18nVO systemI18nVO = CopyUtil.copyBean(systemI18nConfig, SystemI18nVO::new);
        return Result.ok(systemI18nVO);
    }

    @GetMapping("lang/list")
    public Result<List<SystemI18nVO>> listLang() {
        List<SystemI18nConfig> systemI18nConfigs = systemI18nConfigService.listAll(SystemI18nConfig::getId, SystemI18nConfig::getLang, SystemI18nConfig::getDescription);
        List<SystemI18nVO> systemI18nLangVOS = CopyUtil.copyList(systemI18nConfigs, SystemI18nVO::new);
        return Result.ok(systemI18nLangVOS);
    }

    @PostMapping("lang/setDefaultLang")
    public Result<?> setDefaultLang(String lang) {
        if (StringUtils.isEmpty(lang)) {
            lang = "zh-CN";
        }
        systemConfigService.setConfig(EnvironmentKeys.TORNA_DEFAULT_LANG.getKey(), lang, "系统默认语言");
        return Result.ok();
    }

    @PostMapping("save")
    public Result<IdVO> save(@RequestBody SystemI18nConfigParam param) {
        SystemI18nConfig systemI18nConfig = CopyUtil.copyBean(param, SystemI18nConfig::new);
        systemI18nConfigService.saveOrUpdate(systemI18nConfig);
        return Result.ok(new IdVO(systemI18nConfig.getId()));
    }

    @PostMapping("delete")
    public Result save(@RequestBody IdParam param) {
        systemI18nConfigService.deleteById(param.getId());
        return Result.ok();
    }

}
