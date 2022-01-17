package cn.torna.web.controller.module.vo;

import cn.torna.service.dto.DocParamDTO;
import lombok.Data;

import java.util.List;

/**
 * @author thc
 */
@Data
public class ModuleGlobalParamsVO {
    private List<DocParamDTO> globalHeaders;
    private List<DocParamDTO> globalParams;
    private List<DocParamDTO> globalReturns;
}
