package cn.torna.swaggerplugin.bean;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.Optional;

/**
 * @author tanghc
 */
public class ApiParamWrapper {
    private final Optional<ApiParam> apiParamOptional;

    public ApiParamWrapper(ApiParam apiParam) {
        this.apiParamOptional = Optional.ofNullable(apiParam);
    }

    public String getType() {
        return apiParamOptional.map(ApiParam::type).orElse("");
    }

    public String getExample() {
        return apiParamOptional.map(ApiParam::example).orElse("");
    }

    public String getDescription() {
        return apiParamOptional.map(ApiParam::value).orElse("");
    }

    public boolean getRequired() {
        return apiParamOptional.map(ApiParam::required).orElse(false);
    }
}
