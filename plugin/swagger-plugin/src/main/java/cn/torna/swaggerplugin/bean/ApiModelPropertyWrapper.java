package cn.torna.swaggerplugin.bean;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * @author tanghc
 */
public class ApiModelPropertyWrapper {
    private final Optional<ApiModelProperty> apiModelPropertyOptional;
    private final Optional<Field> fieldOptional;

    public ApiModelPropertyWrapper(ApiModelProperty apiModelPropertyOptional, Field field) {
        this.apiModelPropertyOptional = Optional.ofNullable(apiModelPropertyOptional);
        this.fieldOptional = Optional.ofNullable(field);
    }

    public String getName() {
        String name = apiModelPropertyOptional.map(ApiModelProperty::name).orElse(null);
        if (StringUtils.isEmpty(name)) {
            name = fieldOptional.map(Field::getName).orElse("");
        }
        return name;
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
        String dataType = apiModelPropertyOptional.map(ApiModelProperty::dataType).orElse(null);
        if (StringUtils.isEmpty(dataType)) {
            dataType = fieldOptional.map(Field::getType).map(Class::getSimpleName).orElse("");
        }
        return dataType;
    }
}
