package torna.api.open;

import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import org.springframework.beans.factory.annotation.Autowired;
import torna.api.bean.RequestContext;
import torna.api.open.param.EnumInfoCreateParam;
import torna.common.util.json.JsonUtil;
import torna.service.EnumService;
import torna.service.dto.EnumInfoDTO;

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
        EnumInfoDTO enumInfoDTO = JsonUtil.parseObject(JsonUtil.toJSONString(param), EnumInfoDTO.class);
        enumInfoDTO.setModuleId(moduleId);
        enumService.saveEnumInfo(enumInfoDTO);
    }

}
