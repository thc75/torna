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

    /* torna配置文件名称，默认找classpath下的torna.json文件 */
    private String configFile = "torna.json";

    /** 开启推送 */
    private Boolean enable = false;
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
    private Boolean debug = false;
    /** 接口多个method只显示 */
    private String methodWhenMulti = "GET";
    /** 具有body体的方法 */
    private String hasBodyMethods = "POST,PUT,DELETE";
    /** 是否替换文档，true：替换，false：不替换（追加）。默认：true */
    private Boolean isReplace = true;
    /** 0:springmvc, 1:dubbo或其它.如果是1，必须指定basePackage */
    private int mode = 0;
    /** 全局的contentType */
    private String globalContentType = "application/json";
    /** 默认的http method */
    private String defaultHttpMethod = "POST";

}
