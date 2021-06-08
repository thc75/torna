package cn.torna.swaggerplugin.bean;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class TornaConfig {

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
    private String debug;
    /** 接口多个method只显示 */
    private String methodWhenMulti;

    /** path参数名称 */
    private String pathName;
    /** header参数名称 */
    private String headerName;
    /** query参数名称 */
    private String queryName;
    /** form参数名称 */
    private String formName;
    /** body参数名称 */
    private String bodyName;
    /** 具有body体的方法 */
    private String hasBodyMethods = "POST,PUT,DELETE";

}
