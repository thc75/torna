package cn.torna.swaggerplugin.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author tanghc
 */
@Data
@ConfigurationProperties(prefix = TornaProperties.PREFIX)
public class TornaProperties {

    public static final String PREFIX = "torna.swagger-plugin";

    /** 开启推送 */
    private String enable;
    /** 扫描package，多个用";"隔开。不指定扫描全部 */
    private String basePackage;
    /** 推送URL */
    private String url;
    /** appKey */
    private String appKey;
    /** secret */
    private String secret;
    /** 模块token */
    private String token;

    /** 调试环境，格式：环境名称,调试路径，多个用"|"隔开 */
    private String debugEnv;
    /** 推送人 */
    private String author;
    /** 打开调试:true/false */
    private String debug = "false";
    /** 接口多个method只显示 */
    private String methodWhenMulti = "GET";

    /** path参数名称 */
    private String pathName = "path";
    /** header参数名称 */
    private String headerName = "header";
    /** query参数名称 */
    private String queryName = "query";
    /** form参数名称 */
    private String formName = "form";
    /** body参数名称 */
    private String bodyName = "body";

}
