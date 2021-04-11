package cn.torna.api.open;

import cn.torna.api.bean.RequestContext;
import cn.torna.api.open.param.EnumInfoCreateParam;
import cn.torna.common.util.CopyUtil;
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
    @ApiDocMethod(description = "推送字典")
    public void push(EnumInfoCreateParam param) {
        long moduleId = RequestContext.getCurrentContext().getModuleId();
        EnumInfoDTO enumInfoDTO = CopyUtil.deepCopy(param, EnumInfoDTO.class);
        enumInfoDTO.setModuleId(moduleId);
        enumService.saveEnumInfo(enumInfoDTO);
    }

}
