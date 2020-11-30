package torna.api.open.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import torna.common.support.IdCodec;

import java.util.List;

/**
 * @author tanghc
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DocInfoDetailUpdateDTO extends DocInfoDetailCreateDTO {
    @ApiDocField(description = "文档id", example = "je24ozLJ", required = true)
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

}
