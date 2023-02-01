package cn.torna.web.controller.admin;

import cn.torna.common.bean.Result;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.SystemI18nConfig;
import cn.torna.service.SystemI18nConfigService;
import cn.torna.web.controller.admin.param.SystemI18nConfigParam;
import cn.torna.web.controller.admin.vo.SystemI18nLangVO;
import com.alibaba.fastjson.JSONObject;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 国际化配置
 * @author thc
 */
@RestController
@RequestMapping("admin/i18n")
public class I18nController {

    @Autowired
    private SystemI18nConfigService systemI18nConfigService;

    @GetMapping("content/get")
    public Result<Object> getLangContent(String lang) {
        JSONObject content = systemI18nConfigService.getContentByLang(lang);
        return Result.ok(content);
    }

    @GetMapping("lang/list")
    public Result<List<SystemI18nLangVO>> listLang() {
        List<SystemI18nConfig> systemI18nConfigs = systemI18nConfigService.getMapper()
                .listBySpecifiedColumns(Arrays.asList("lang", "description"), new Query());
        List<SystemI18nLangVO> systemI18nLangVOS = CopyUtil.copyList(systemI18nConfigs, SystemI18nLangVO::new);
        return Result.ok(systemI18nLangVOS);
    }

    @PostMapping("save")
    public Result save(@RequestBody SystemI18nConfigParam param) {
        SystemI18nConfig systemI18nConfig = CopyUtil.copyBean(param, SystemI18nConfig::new);
        SystemI18nConfig config = systemI18nConfigService.get("lang", param.getLang());
        if (config == null) {
            systemI18nConfigService.save(systemI18nConfig);
        } else {
            CopyUtil.copyPropertiesIgnoreNull(systemI18nConfig, config);
            systemI18nConfigService.update(config);
        }
        return Result.ok();
    }

}
