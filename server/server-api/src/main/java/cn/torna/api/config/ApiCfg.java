package cn.torna.api.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gitee.easyopen.ApiConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tanghc
 */
@Configuration
public class ApiCfg {

    @Bean
    public ApiConfig apiConfig() {
        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setJsonResultSerializer(obj -> JSON.toJSONString(obj,
                SerializerFeature.WriteNullStringAsEmpty
                , SerializerFeature.WriteMapNullValue
                , SerializerFeature.WriteDateUseDateFormat)
        );
        return apiConfig;
    }


}
