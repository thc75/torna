package cn.torna.api.config;

import cn.torna.api.manager.ApiAppSecretManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gitee.easyopen.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tanghc
 */
@Configuration
public class ApiCfg {

    @Autowired
    private ApiAppSecretManager apiAppSecretManager;

    @Bean
    public ApiConfig apiConfig() {
        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setJsonResultSerializer(obj -> JSON.toJSONString(obj,
                SerializerFeature.WriteNullStringAsEmpty
                , SerializerFeature.WriteMapNullValue
                , SerializerFeature.WriteDateUseDateFormat)
        );
        apiConfig.setAppSecretManager(apiAppSecretManager);
        return apiConfig;
    }


}
