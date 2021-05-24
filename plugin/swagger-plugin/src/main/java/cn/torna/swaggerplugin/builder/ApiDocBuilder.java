package cn.torna.swaggerplugin.builder;

import cn.torna.swaggerplugin.util.PluginUtil;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author tanghc
 */
public class ApiDocBuilder {

    private Class<?> lastClass;
    private int loopCount;
    
    public List<ApiDocFieldDefinition> buildApiDocFieldDefinition(Class<?> paramClass) {
        return buildApiDocFieldDefinitionsByType(paramClass, true);
    }

    /** 从api参数中构建 */
    protected List<ApiDocFieldDefinition> buildApiDocFieldDefinitionsByType(Class<?> clazz, boolean root) {
        if (clazz.isInterface()) {
            return Collections.emptyList();
        }
        // 解决循环注解导致死循环问题
        this.addCycle(clazz);

        final List<ApiDocFieldDefinition> docDefinition = new ArrayList<ApiDocFieldDefinition>();

        // 遍历参数对象中的属性
        ReflectionUtils.doWithFields(clazz, field -> {
            ApiModelProperty apiModelProperty = AnnotationUtils.findAnnotation(field, ApiModelProperty.class);
            // 找到有注解的属性
            if (apiModelProperty != null && !apiModelProperty.hidden()) {
                if (root) {
                    resetCycle();
                }
                ApiDocFieldDefinition fieldDefinition;
                Class<?> fieldType = field.getType();
                if (PluginUtil.isPojo(fieldType)) {
                    // 如果是自定义类
                    fieldDefinition = buildApiDocFieldDefinitionByClass(apiModelProperty, fieldType, field);
                } else {
                    fieldDefinition = buildApiDocFieldDefinition(apiModelProperty, field);
                }
                docDefinition.add(fieldDefinition);
            }
        });

        return docDefinition;
    }

    protected ApiDocFieldDefinition buildApiDocFieldDefinition(ApiModelProperty apiModelProperty, Field field) {
        String type = getFieldType(field, apiModelProperty);
        // 优先使用注解中的字段名
        String fieldName = getFieldName(field, apiModelProperty);
        String description = getFieldDescription(apiModelProperty);
        boolean required = apiModelProperty.required();
        String example = apiModelProperty.example();

        ApiDocFieldDefinition fieldDefinition = new ApiDocFieldDefinition();
        fieldDefinition.setName(fieldName);
        fieldDefinition.setType(type);
        fieldDefinition.setRequired(getRequiredValue(required));
        fieldDefinition.setExample(example);
        fieldDefinition.setDescription(description);
        //fieldDefinition.setMaxLength(buildMaxLength(apiModelProperty, field));

//        List<ApiDocFieldDefinition> elementsDefinition = loopCount < 1 ? buildElementListDefinition(apiModelProperty) : Collections.emptyList();
//        fieldDefinition.setElements(elementsDefinition);
//        fieldDefinition.setElementClass(apiModelProperty.elementClass());
//
//        if (elementsDefinition.size() > 0) {
//            fieldDefinition.setDataType(DataType.ARRAY.getValue());
//        }

        return fieldDefinition;
    }

    protected ApiDocFieldDefinition buildApiDocFieldDefinitionByClass(ApiModelProperty apiModelProperty, Class<?> clazz, Field field) {
        Objects.requireNonNull(apiModelProperty);
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(field);
        String name = getFieldName(field, apiModelProperty);
        String type = DataType.OBJECT.getValue();
        String description = getFieldDescription(apiModelProperty);
        boolean required = apiModelProperty.required();
        String example = apiModelProperty.example();

        ApiDocFieldDefinition fieldDefinition = new ApiDocFieldDefinition();
        fieldDefinition.setName(name);
        fieldDefinition.setType(type);
        fieldDefinition.setRequired(getRequiredValue(required));
        fieldDefinition.setExample(example);
        fieldDefinition.setDescription(description);
        //fieldDefinition.setMaxLength(buildMaxLength(apiModelProperty, field));

        List<ApiDocFieldDefinition> children = buildApiDocFieldDefinitionsByType(clazz, false);
        fieldDefinition.setChildren(children);

        return fieldDefinition;
    }

    private static byte getRequiredValue(boolean b) {
        return (byte)(b ? 1 : 0);
    }

    private static String getFieldType(Field field, ApiModelProperty apiModelProperty) {
        String dataType = apiModelProperty.dataType();
        if (StringUtils.hasText(dataType)) {
            return dataType;
        }
        if (field == null) {
            return DataType.STRING.getValue();
        }
        Class<?> type = field.getType();
        if (type == List.class || type == Collection.class || type == Set.class || type.isArray()) {
            return DataType.ARRAY.getValue();
        }
        if(type == Date.class) {
            return DataType.DATE.getValue();
        }
        if(type == Timestamp.class) {
            return DataType.DATETIME.getValue();
        }
        if (field.getName().contains("MultipartFile")) {
           return DataType.FILE.getValue();
        }
        return field.getType().getSimpleName().toLowerCase();
    }

    private static String getFieldName(Field field, ApiModelProperty apiModelProperty) {
        String name = apiModelProperty.name();
        if (StringUtils.hasText(name)) {
            return name;
        }
        return field.getName();
    }

    private static String getFieldDescription(ApiModelProperty apiModelProperty) {
        String desc = apiModelProperty.value();
        if (StringUtils.isEmpty(desc)) {
            desc = apiModelProperty.notes();
        }
        return desc;
    }

    private void addCycle(Class<?> clazz) {
        if (lastClass == clazz) {
            loopCount++;
        } else {
            loopCount = 0;
        }
        lastClass = clazz;
    }

    private void resetCycle() {
        loopCount = 0;
    }
}
