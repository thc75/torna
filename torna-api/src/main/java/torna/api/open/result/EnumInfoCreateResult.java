package torna.api.open.result;

import com.alibaba.fastjson.annotation.JSONField;
import com.gitee.easyopen.doc.DataType;
import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import torna.common.support.IdCodec;

import javax.validation.constraints.NotBlank;

/**
 * @author tanghc
 */
@Data
public class EnumInfoCreateResult {

    @ApiDocField(description = "字典分类id", dataType = DataType.STRING, example = "Br2jqzG7")
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

}
