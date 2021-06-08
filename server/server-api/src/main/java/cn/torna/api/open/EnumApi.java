package cn.torna.api.open;

import cn.torna.api.bean.RequestContext;
import cn.torna.api.open.param.EnumInfoCreateBatchParam;
import cn.torna.api.open.param.EnumInfoCreateParam;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.ThreadPoolUtil;
import cn.torna.service.EnumService;
import cn.torna.service.dto.EnumInfoDTO;
import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author tanghc
 */
@ApiService
@ApiDoc(value = "字典API", order = 2)
public class EnumApi {

    @Autowired
    private EnumService enumService;

    @Api(name = "enum.push")
    @ApiDocMethod(description = "推送字典", order = 1)
    public void push(EnumInfoCreateParam param) {
        long moduleId = RequestContext.getCurrentContext().getModuleId();
        EnumInfoDTO enumInfoDTO = CopyUtil.deepCopy(param, EnumInfoDTO.class);
        enumInfoDTO.setModuleId(moduleId);
        enumService.saveEnumInfo(enumInfoDTO);
    }

    @Api(name = "enum.batch.push")
    @ApiDocMethod(description = "批量推送字典", order = 2)
    public void batchPush(EnumInfoCreateBatchParam param) {
        long moduleId = RequestContext.getCurrentContext().getModuleId();
        ThreadPoolUtil.execute(() -> {
            for (EnumInfoCreateParam enumInfoCreateParam : param.getEnums()) {
                EnumInfoDTO enumInfoDTO = CopyUtil.deepCopy(enumInfoCreateParam, EnumInfoDTO.class);
                enumInfoDTO.setModuleId(moduleId);
                enumService.saveEnumInfo(enumInfoDTO);
            }
        });
    }

}
