package cn.torna.web.controller.system;

import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.Configs;
import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.bean.Result;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.SystemI18nConfig;
import cn.torna.service.SystemI18nConfigService;
import cn.torna.web.controller.admin.vo.SystemI18nVO;
import cn.torna.web.controller.system.vo.LangVO;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author thc
 */
@RestController
@RequestMapping("system/i18n")
public class SystemI18nController {

    @Autowired
    private SystemI18nConfigService systemI18nConfigService;


    @NoLogin
    @GetMapping("lang/list")
    public Result<List<SystemI18nVO>> listLang() {
        List<SystemI18nConfig> systemI18nConfigs = systemI18nConfigService.getMapper()
                .listAll(SystemI18nConfig::getId, SystemI18nConfig::getLang, SystemI18nConfig::getDescription);
        List<SystemI18nVO> systemI18nLangVOS = CopyUtil.copyList(systemI18nConfigs, SystemI18nVO::new);
        return Result.ok(systemI18nLangVOS);
    }

    @NoLogin
    @GetMapping("lang/default")
    public Result<String> defaultLang() {
        String lang = EnvironmentKeys.TORNA_DEFAULT_LANG.getValue();
        return Result.ok(lang);
    }

    @NoLogin
    @GetMapping("get")
    public Result<LangVO> getLangContent(String lang) {
        JSONObject jsonObject = systemI18nConfigService.getContentByLang(lang);
        String defaultLang = Configs.getValue("torna.default-lang", "zh-CN");
        return Result.ok(new LangVO(jsonObject, defaultLang, lang));
    }
}
