package cn.torna.swaggerplugin.bean;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author tanghc
 */
public class ApiModelPropertyWrapper {
    private final Optional<ApiModelProperty> apiModelPropertyOptional;

    public ApiModelPropertyWrapper(ApiModelProperty apiModelProperty) {
        this.apiModelPropertyOptional = Optional.ofNullable(apiModelProperty);
    }

    public String getName() {
        return apiModelPropertyOptional.map(ApiModelProperty::name).orElse("");
    }

    public String getValue() {
        return apiModelPropertyOptional.map(ApiModelProperty::value).orElse("");
    }

    public String getDescription() {
        String value = getValue();
        String note = apiModelPropertyOptional.map(ApiModelProperty::notes).orElse("");
        if (StringUtils.hasText(note)) {
            value = value + "。Note：" + note;
        }
        return value;
    }

    public boolean getRequired() {
        return apiModelPropertyOptional.map(ApiModelProperty::required).orElse(false);
    }

    public String getExample() {
        return apiModelPropertyOptional.map(ApiModelProperty::example).orElse("");
    }

    public int getPosition() {
        return apiModelPropertyOptional.map(ApiModelProperty::position).orElse(0);
    }

    public String getDataType() {
        return apiModelPropertyOptional.map(ApiModelProperty::dataType).orElse("");
    }
}
