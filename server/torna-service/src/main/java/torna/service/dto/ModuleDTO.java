package torna.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.support.IdCodec;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class ModuleDTO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    private String name;

    private List<DocInfoDTO> docInfoList;

}
