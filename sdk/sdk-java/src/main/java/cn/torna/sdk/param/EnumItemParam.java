package cn.torna.sdk.param;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnumItemParam {

    /** 名称，字面值 */
    private String name;

    /** 类型 */
    private String type;

    /** 枚举值 */
    private String value;

    /** 枚举描述 */
    private String description = "";
}