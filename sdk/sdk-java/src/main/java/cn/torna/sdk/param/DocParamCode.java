package cn.torna.sdk.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tanghc
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocParamCode {

    /** code名称 */
    private String code;

    /** 错误描述 */
    private String msg;

    /** 解决方案 */
    private String solution;



}
