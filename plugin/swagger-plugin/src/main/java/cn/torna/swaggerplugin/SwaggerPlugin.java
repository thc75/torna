package cn.torna.swaggerplugin;

import cn.torna.swaggerplugin.bean.TornaConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 文档推送辅助类
 * @author thc
 */
public class SwaggerPlugin {

    /**
     * 推送文档，前提：把<code>torna.json</code>文件复制到resources下
     */
    public static void pushDoc() {
        pushDoc("torna.json");
    }

    /**
     * 推送swagger文档
     * @param configFile 配置文件
     */
    public static void pushDoc(String configFile) {
        ClassPathResource classPathResource = new ClassPathResource(configFile);
        if (!classPathResource.exists()) {
            throw new IllegalArgumentException("找不到文件:" + configFile + "，请确保resources下有torna.json");
        }
        System.out.println("加载Torna配置文件:" + configFile);
        try {
            InputStream inputStream = classPathResource.getInputStream();
            String json = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            JSONObject jsonObject = JSON.parseObject(json);
            TornaConfig tornaConfig = jsonObject.toJavaObject(TornaConfig.class);
            new SwaggerPluginService(tornaConfig).pushDoc();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("推送文档出错", e);
        }
    }

}
