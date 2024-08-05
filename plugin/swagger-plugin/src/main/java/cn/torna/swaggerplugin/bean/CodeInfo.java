package cn.torna.swaggerplugin.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author tanghc
 */
@Getter
@Setter
public class CodeInfo {

    /** 枚举名称 */
    private String name;

    /** 枚举说明 */
    private String description;

    /** 元素类型 */
    private String itemType;

    /** 枚举项 */
    private List<CodeItem> items;

}
