package cn.torna.swaggerplugin.bean;

import cn.torna.swaggerplugin.util.PluginUtil;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author tanghc
 */
public class ApiModelPropertyWrapper {

    private static final List<String> NOT_NULL_ANNOTATIONS = Arrays.asList(
            "NotNull",
            "NotBlank",
            "NotEmpty"
    );

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
        if (fieldOptional.isPresent()) {
            Field field = fieldOptional.get();
            boolean hasAnyAnnotation = PluginUtil.hasAnyAnnotation(field, NOT_NULL_ANNOTATIONS);
            // 如果有不为null相关的注解，直接返回true，必填
            if (hasAnyAnnotation) {
                return true;
            }
        }
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

    public String getMaxLength() {
        if (!fieldOptional.isPresent()) {
            return "-";
        }
        Field field = fieldOptional.get();
        if (PluginUtil.hasAnyAnnotation(field, Collections.singletonList("Length"))) {
            org.hibernate.validator.constraints.Length length = field.getAnnotation(org.hibernate.validator.constraints.Length.class);
            return String.valueOf(length.max());
        }
        if (PluginUtil.hasAnyAnnotation(field, Collections.singletonList("Max"))) {
            javax.validation.constraints.Max max = field.getAnnotation(javax.validation.constraints.Max.class);
            return String.valueOf(max.value());
        }
        return "-";
    }

}
