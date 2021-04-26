package cn.torna.sdk.param;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tanghc
 */
@Getter
@Setter
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
