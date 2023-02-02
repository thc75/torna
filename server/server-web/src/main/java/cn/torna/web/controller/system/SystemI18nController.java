package cn.torna.web.controller.system;

import cn.torna.common.annotation.HashId;
import cn.torna.common.annotation.NoLogin;
import cn.torna.common.bean.Result;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.SystemI18nConfig;
import cn.torna.service.SystemI18nConfigService;
import cn.torna.web.controller.admin.vo.SystemI18nVO;
import com.alibaba.fastjson.JSONObject;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
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
                .listBySpecifiedColumns(Arrays.asList("id", "lang", "description"), new Query());
        List<SystemI18nVO> systemI18nLangVOS = CopyUtil.copyList(systemI18nConfigs, SystemI18nVO::new);
        return Result.ok(systemI18nLangVOS);
    }

    @NoLogin
    @GetMapping("get")
    public Result<JSONObject> getLangContent(String lang) {
        JSONObject jsonObject = systemI18nConfigService.getContentByLang(lang);
        return Result.ok(jsonObject);
    }
}
