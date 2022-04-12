package cn.torna.sdk.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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