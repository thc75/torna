package cn.torna.swaggerplugin.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class TornaConfig {

    private String configFile;

    /**
     * 开启推送
     */
    private Boolean enable = false;
    /**
     * 扫描package，多个用";"隔开。不指定扫描全部
     */
    private String basePackage;
    /**
     * 指定扫描的API，可以是某个controller，也可以是某个接口方法
     */
    private List<String> scanApis;
    /**
     * 推送URL
     */
    private String url;
    /**
     * 模块token
     */
    private String token;
    /**
     * 调试环境，格式：环境名称,调试路径，多个用"|"隔开
     */
    private String debugEnv;
    /**
     * 推送人
     */
    private String author;
    /**
     * 打开调试:true/false
     */
    private Boolean debug = true;
    /**
     * 调试输出json格式化
     */
    private Boolean debugPrintFormat = false;
    /**
     * 接口多个method只显示
     */
    private String methodWhenMulti = "GET";
    /**
     * 具有body体的方法
     */
    private String hasBodyMethods = "POST,PUT,DELETE";
    /**
     * 是否替换文档，true：替换，false：不替换（追加）。默认：true
     */
    private Boolean isReplace = true;

    /**
     * 0:springmvc, 1:dubbo
     */
    private int mode;

    /**
     * 全局的contentType
     */
    private String globalContentType = "application/x-www-form-urlencoded";
    /**
     * 默认的http method
     */
    private String defaultHttpMethod = "POST";

    /**
     * 第三方jar
     */
    private JSONObject jarClass;

    /**
     * 定义错误码
     */
    private List<CodeInfo> codes;

    /**
     * 只能是object类型对象
     * 如：com.alibaba.fastjson.JSONObject
     */
    private List<String> objectClassList;

}
