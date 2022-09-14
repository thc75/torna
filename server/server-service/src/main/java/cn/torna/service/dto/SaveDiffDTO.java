package cn.torna.service.dto;

import cn.torna.common.enums.ModifyType;
import cn.torna.common.enums.PositionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author thc
 */
@Builder
@Getter
@Setter
public class SaveDiffDTO {

    private Long operateKey;

    private String dataId;

    private PositionType positionType;

    private ModifyType modifyType;

    private String targetName;

    private String oldValue;

    private String newValue;

}
