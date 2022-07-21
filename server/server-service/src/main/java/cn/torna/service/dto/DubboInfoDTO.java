package cn.torna.service.dto;

import lombok.Data;

/**
 * @author thc
 */
@Data
public class DubboInfoDTO {

    private String protocol;
    private String dependency;
    private String author;
    private String interfaceName;

}
