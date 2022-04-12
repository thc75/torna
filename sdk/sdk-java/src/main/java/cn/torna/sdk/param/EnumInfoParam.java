package cn.torna.sdk.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author tanghc
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumInfoParam {

    /** 枚举名称 */
    private String name;

    /** 枚举说明 */
    private String description;

    /** 枚举项 */
    private List<EnumItemParam> items;

}
