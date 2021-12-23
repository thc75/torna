package cn.torna.api.open;

import cn.torna.api.bean.RequestContext;
import cn.torna.api.open.param.DebugEnvDeleteParam;
import cn.torna.api.open.param.DebugEnvParam;
import cn.torna.service.ModuleEnvironmentService;
import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author tanghc
 */
@ApiService
@ApiDoc(value = "模块API", order = 3)
@Slf4j
public class ModuleApi {

    @Autowired
    private ModuleEnvironmentService moduleEnvironmentService;

    @Api(name = "module.debug.env.set")
    @ApiDocMethod(description = "设置调试环境", order = 1
            , remark = "新增或修改调试环境"
    )
    public void setDebugEnv(DebugEnvParam param) {
        long moduleId = RequestContext.getCurrentContext().getModuleId();
        moduleEnvironmentService.setDebugEnv(moduleId, param.getName(), param.getUrl());
    }

    @Api(name = "module.debug.env.delete")
    @ApiDocMethod(description = "删除调试环境", order = 2)
    public void delDebugEnv(DebugEnvDeleteParam param) {
        long moduleId = RequestContext.getCurrentContext().getModuleId();
        moduleEnvironmentService.deleteDebugEnv(moduleId, param.getName());
    }

}
