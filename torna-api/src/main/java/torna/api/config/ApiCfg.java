package torna.api.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gitee.easyopen.ApiConfig;
import com.gitee.easyopen.spring.boot.autoconfigure.EasyopenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import torna.api.manager.ApiAppSecretManager;

/**
 * @author tanghc
 */
@Configuration
public class ApiCfg {

    @Autowired
    private ApiAppSecretManager apiAppSecretManager;

    @Autowired
    private EasyopenProperties easyopenProperties;

    @Bean
    public ApiConfig apiConfig() {
        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setJsonResultSerializer(obj -> JSON.toJSONString(obj,
                SerializerFeature.WriteNullStringAsEmpty
                , SerializerFeature.WriteMapNullValue
                , SerializerFeature.WriteDateUseDateFormat)
        );
        apiConfig.setAppSecretManager(apiAppSecretManager);
        apiConfig.setTimeoutSeconds(0);
        if (easyopenProperties.isShowDoc()) {
            String docDir = System.getProperty("user.dir") + "/markdowndoc";
            easyopenProperties.setMarkdownDocDir(docDir);
        }
        return apiConfig;
    }


}
