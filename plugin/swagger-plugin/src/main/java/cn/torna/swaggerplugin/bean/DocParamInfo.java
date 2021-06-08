package cn.torna.swaggerplugin.bean;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class DocParamInfo {

    /** 示例值 */
    private String example;

    /** 描述 */
    private String description;

    /** 数据类型 */
    private String type;

    /** 必填 */
    private Boolean required;
}
