package cn.torna.sdk.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author tanghc
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DubboInfo {

    /** 接口名 */
    private String interfaceName;

    /** 版本号 */
    private String version;

    /** 作者 */
    private String author;

    /** 协议 */
    private String protocol;

    /** maven依赖 */
    private String dependency;
}
