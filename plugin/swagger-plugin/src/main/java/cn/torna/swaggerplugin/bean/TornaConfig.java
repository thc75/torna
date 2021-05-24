package cn.torna.swaggerplugin.bean;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class TornaConfig {

    private String url;
    private String appKey;
    private String secret;
    private String token;

    // 测试环境,http://10.0.10.1
    private String debugEnv;
    private String author;
    private String debug;

    private String methodWhenMulti;

    private String pathName;
    private String headerName;
    private String queryName;
    private String formName;
    private String bodyName;

}
