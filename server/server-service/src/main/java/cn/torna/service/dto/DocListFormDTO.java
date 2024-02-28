package cn.torna.service.dto;

import com.gitee.fastmybatis.core.query.annotation.Condition;
import lombok.Data;

/**
 * @author thc
 */
@Data
public class DocListFormDTO {

    @Condition
    private Long moduleId;

    @Condition
    private Byte status;

}
