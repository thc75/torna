package cn.torna.swaggerplugin.builder;

import cn.torna.swaggerplugin.util.PluginUtil;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 解析文档信息
 * @author tanghc
 */
public class ApiDocBuilder {

    private Class<?> lastClass;
    private int loopCount;

    /**
     * 生成文档信息
     * @param paramClass 参数类型
     * @return 返回文档内容
     */
    public List<FieldDocInfo> buildFieldDocInfo(Class<?> paramClass) {
        return buildFieldDocInfosByType(paramClass, true);
    }

    /**
     * 从api参数中构建
     */
    protected List<FieldDocInfo> buildFieldDocInfosByType(Class<?> clazz, boolean root) {
        if (clazz.isInterface()) {
            return Collections.emptyList();
        }
        // 解决循环注解导致死循环问题
        this.addCycle(clazz);
        final List<FieldDocInfo> fieldDocInfos = new ArrayList<>();
        // 遍历参数对象中的属性
        ReflectionUtils.doWithFields(clazz, field -> {
            ApiModelProperty apiModelProperty = AnnotationUtils.findAnnotation(field, ApiModelProperty.class);
            // 找到有注解的属性
            if (apiModelProperty != null && !apiModelProperty.hidden()) {
                if (root) {
                    resetCycle();
                }
                FieldDocInfo fieldDocInfo;
                Class<?> fieldType = field.getType();
                if (PluginUtil.isPojo(fieldType)) {
                    // 如果是自定义类
                    fieldDocInfo = buildFieldDocInfoByClass(apiModelProperty, fieldType, field);
                } else {
                    fieldDocInfo = buildFieldDocInfo(apiModelProperty, field);
                }
                fieldDocInfos.add(fieldDocInfo);
            }
        });
        return fieldDocInfos;
    }

    protected FieldDocInfo buildFieldDocInfo(ApiModelProperty apiModelProperty, Field field) {
        String type = getFieldType(field, apiModelProperty);
        // 优先使用注解中的字段名
        String fieldName = getFieldName(field, apiModelProperty);
        String description = getFieldDescription(apiModelProperty);
        boolean required = apiModelProperty.required();
        String example = apiModelProperty.example();

        FieldDocInfo fieldDocInfo = new FieldDocInfo();
        fieldDocInfo.setName(fieldName);
        fieldDocInfo.setType(type);
        fieldDocInfo.setRequired(getRequiredValue(required));
        fieldDocInfo.setExample(example);
        fieldDocInfo.setDescription(description);
        fieldDocInfo.setOrderIndex(apiModelProperty.position());

        Class<?> fieldType = field.getType();
        Class<?> elementClass = null;
        if (Collection.class.isAssignableFrom(fieldType)) {
            Type genericType = field.getGenericType();
            elementClass = PluginUtil.getGenericType(genericType);
        } else if (fieldType.isArray()) {
            elementClass = fieldType.getComponentType();
        }
        if (elementClass != null && elementClass != Object.class && elementClass != Void.class) {
            if (PluginUtil.isPojo(elementClass)) {
                List<FieldDocInfo> fieldDocInfos = loopCount < 1
                        ? buildFieldDocInfosByType(elementClass, false)
                        : Collections.emptyList();
                fieldDocInfo.setChildren(fieldDocInfos);
            }
        }
        return fieldDocInfo;
    }

    protected FieldDocInfo buildFieldDocInfoByClass(ApiModelProperty apiModelProperty, Class<?> clazz, Field field) {
        Objects.requireNonNull(apiModelProperty);
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(field);
        String name = getFieldName(field, apiModelProperty);
        String type = DataType.OBJECT.getValue();
        String description = getFieldDescription(apiModelProperty);
        boolean required = apiModelProperty.required();
        String example = apiModelProperty.example();

        FieldDocInfo fieldDocInfo = new FieldDocInfo();
        fieldDocInfo.setName(name);
        fieldDocInfo.setType(type);
        fieldDocInfo.setRequired(getRequiredValue(required));
        fieldDocInfo.setExample(example);
        fieldDocInfo.setDescription(description);
        fieldDocInfo.setOrderIndex(apiModelProperty.position());

        List<FieldDocInfo> children = buildFieldDocInfosByType(clazz, false);
        fieldDocInfo.setChildren(children);

        return fieldDocInfo;
    }

    private static byte getRequiredValue(boolean b) {
        return (byte) (b ? 1 : 0);
    }

    private static String getFieldType(Field field, ApiModelProperty apiModelProperty) {
        String dataType = apiModelProperty.dataType();
        if (StringUtils.hasText(dataType)) {
            return dataType;
        }
        Class<?> type = field.getType();
        if (Collection.class.isAssignableFrom(type) || type.isArray()) {
            return DataType.ARRAY.getValue();
        }
        if (type == Date.class) {
            return DataType.DATE.getValue();
        }
        if (type == Timestamp.class) {
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
