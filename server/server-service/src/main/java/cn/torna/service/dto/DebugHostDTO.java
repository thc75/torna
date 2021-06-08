package cn.torna.service.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tanghc
 */
@Setter
@Getter
public class DebugHostDTO {

    /** 配置key*/
    private String configKey;

    /** 配置值 */
    private String configValue;

    private Long extendId;
}
