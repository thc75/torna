package cn.torna.swaggerplugin.builder;

import cn.torna.swaggerplugin.bean.ApiModelPropertyWrapper;
import cn.torna.swaggerplugin.bean.ApiParamInfo;
import cn.torna.swaggerplugin.bean.Booleans;
import cn.torna.swaggerplugin.bean.TornaConfig;
import cn.torna.swaggerplugin.util.PluginUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import sun.reflect.generics.repository.ClassRepository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static cn.torna.swaggerplugin.util.PluginUtil.getGenericParamKey;

/**
 * 解析文档信息
 * @author tanghc
 */
public class ApiDocBuilder {

    private Class<?> lastClass;
    private int loopCount;
    private Map<String, Class<?>> genericParamMap = Collections.emptyMap();
    private Set<String> hiddenColumns = Collections.emptySet();
    private TornaConfig tornaConfig;

    public ApiDocBuilder() {
    }

    public ApiDocBuilder(Map<String, Class<?>> genericParamMap, TornaConfig tornaConfig) {
        this.tornaConfig = tornaConfig;
        if (genericParamMap == null) {
            genericParamMap = Collections.emptyMap();
        }
        this.genericParamMap = genericParamMap;
        this.initHiddenColumns(tornaConfig);
    }

    private void initHiddenColumns(TornaConfig tornaConfig) {
        JSONObject jarClass = tornaConfig.getJarClass();
        if (jarClass == null) {
            return;
        }
        hiddenColumns = new HashSet<>(8);
        Set<String> classNames = jarClass.keySet();
        for (String className : classNames) {
            /*
            "com.baomidou.mybatisplus.extension.plugins.pagination.Page": {
              "countId": { "value": "xxx", "hidden": true, "required": false, "example": "" }
            }
             */
            JSONObject classConfig = jarClass.getJSONObject(className);
            Set<String> fields = classConfig.keySet();
            for (String field : fields) {
                // { "value": "xxx", "hidden": true, "required": false, "example": "" }
                JSONObject fieldConfig = classConfig.getJSONObject(field);
                Boolean hidden = fieldConfig.getBoolean("hidden");
                if (hidden != null && hidden) {
                    hiddenColumns.add(className + "." + field.trim());
                }
            }
        }
    }

    private ApiParamInfo getApiParamInfo(String className, String fieldName) {
        JSONObject jarClass = tornaConfig.getJarClass();
        if (jarClass == null) {
            return null;
        }
        JSONObject classConfig = jarClass.getJSONObject(className);
        if (classConfig == null)
            return null;
        Set<String> fields = classConfig.keySet();
        for (String field : fields) {
            if (Objects.equals(field, fieldName)) {
                JSONObject fieldConfig = classConfig.getJSONObject(field);
                return fieldConfig == null ? null : fieldConfig.toJavaObject(ApiParamInfo.class);
            }
        }
        return null;
    }

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
        final Class<?> targetClass = PluginUtil.isCollectionOrArray(clazz) ? getCollectionElementClass(clazz) : clazz;
        // 如果是基本类型
        if (!PluginUtil.isPojo(targetClass)) {
            return Collections.emptyList();
        }
        // 解决循环注解导致死循环问题
        this.addCycle(targetClass);
        final List<FieldDocInfo> fieldDocInfos = new ArrayList<>();
        // 遍历参数对象中的属性
        ReflectionUtils.doWithFields(targetClass, field -> {
            ApiModelProperty apiModelProperty = AnnotationUtils.findAnnotation(field, ApiModelProperty.class);
            if (root) {
                resetCycle();
            }
            FieldDocInfo fieldDocInfo;
            Class<?> fieldType = field.getType();
            Type genericType = field.getGenericType();
            Class<?> realClass = null;
            // 如果是未知泛型，private T data
            // 获取真实的类型
            if (genericType instanceof TypeVariable) {
                String typeName = ((TypeVariable<?>) genericType).getName();
                realClass = getGenericParamClass(targetClass, typeName);
            }
            if (realClass != null) {
                fieldDocInfo = buildFieldDocInfoByClass(apiModelProperty, realClass, field);
            } else if (PluginUtil.isPojo(fieldType)) {
                // 如果是自定义类
                fieldDocInfo = buildFieldDocInfoByClass(apiModelProperty, fieldType, field);
            } else {
                fieldDocInfo = buildFieldDocInfo(apiModelProperty, field);
            }
            fieldDocInfos.add(fieldDocInfo);
        }, field -> {
            if (Modifier.isStatic(field.getModifiers()) || isClassFieldHidden(targetClass, field)) {
                return false;
            }
            ApiModelProperty apiModelProperty = AnnotationUtils.findAnnotation(field, ApiModelProperty.class);
            return apiModelProperty == null || !apiModelProperty.hidden();
        });
        this.bindJarClassFields(targetClass, fieldDocInfos);
        return fieldDocInfos;
    }

    protected void bindJarClassFields(Class<?> targetClass, List<FieldDocInfo> fieldDocInfos) {
        for (FieldDocInfo child : fieldDocInfos) {
            ApiParamInfo apiParamInfo = this.getApiParamInfo(targetClass.getName(), child.getName());
            if (apiParamInfo != null) {
                child.setRequired(Booleans.toValue(apiParamInfo.isRequired()));
                child.setExample(apiParamInfo.getExample());
                child.setDescription(apiParamInfo.getValue());
                child.setOrderIndex(apiParamInfo.getPosition());
            }
        }
    }

    protected Class<?> getCollectionElementClass(Class<?> clazz) {
        if (clazz.isArray()) {
            return clazz.getComponentType();
        }
        Class<?> targetClass = clazz;
        Class<? extends Class> rawTypeClass = clazz.getClass();
        Method getGenericInfo = ReflectionUtils.findMethod(rawTypeClass, PluginUtil.METHOD_GET_GENERIC_INFO);
        if (getGenericInfo != null) {
            ReflectionUtils.makeAccessible(getGenericInfo);
            ClassRepository classRepository = (ClassRepository) ReflectionUtils.invokeMethod(getGenericInfo, clazz);
            if (classRepository != null) {
                TypeVariable<?>[] typeParameters = classRepository.getTypeParameters();
                for (TypeVariable<?> typeParameter : typeParameters) {
                    Class<?> realClass = getGenericParamClass(clazz, typeParameter.getName());
                    if (realClass != null) {
                        targetClass = realClass;
                        break;
                    }
                }
            }
        }
        return targetClass;
    }

    protected FieldDocInfo buildFieldDocInfo(ApiModelProperty apiModelProperty, Field field) {
        ApiModelPropertyWrapper apiModelPropertyWrapper = new ApiModelPropertyWrapper(apiModelProperty);
        String type = getFieldType(field, apiModelPropertyWrapper);
        // 优先使用注解中的字段名
        String fieldName = getFieldName(field, apiModelPropertyWrapper);
        String description = getFieldDescription(apiModelProperty);
        boolean required = apiModelPropertyWrapper.getRequired();
        String example = apiModelPropertyWrapper.getExample();

        FieldDocInfo fieldDocInfo = new FieldDocInfo();
        fieldDocInfo.setName(fieldName);
        fieldDocInfo.setType(type);
        fieldDocInfo.setRequired(getRequiredValue(required));
        fieldDocInfo.setExample(example);
        fieldDocInfo.setDescription(description);
        fieldDocInfo.setOrderIndex(apiModelPropertyWrapper.getPosition());

        Class<?> fieldType = field.getType();
        Type genericType = field.getGenericType();
        Type elementClass = null;
        // 如果有明确的泛型，如List<Order>
        if (PluginUtil.isGenericType(genericType)) {
            elementClass = PluginUtil.getGenericType(genericType);
            if (elementClass instanceof TypeVariable) {
                TypeVariable<?> typeVariable = (TypeVariable<?>) elementClass;
                Class<?> genericDeclaration = (Class<?>)typeVariable.getGenericDeclaration();
                Class<?> realClass = this.getGenericParamClass(genericDeclaration, typeVariable.getName());
                if (realClass != null) {
                    elementClass = realClass;
                }
            }
        } else if (fieldType.isArray()) {
            elementClass = fieldType.getComponentType();
        }
        if (elementClass != null && elementClass != Object.class && elementClass != Void.class) {
            Class<?> clazz = (Class<?>) elementClass;
            if (PluginUtil.isPojo(clazz)) {
                List<FieldDocInfo> fieldDocInfos = loopCount < 1
                        ? buildFieldDocInfosByType(clazz, false)
                        : Collections.emptyList();
                fieldDocInfo.setChildren(fieldDocInfos);
            }
        }
        return fieldDocInfo;
    }

    protected Class<?> getGenericParamClass(Class<?> clazz, String name) {
        String genericParamKey = getGenericParamKey(clazz, name);
        return genericParamMap.get(genericParamKey);
    }

    protected FieldDocInfo buildFieldDocInfoByClass(ApiModelProperty apiModelProperty, Class<?> clazz, Field field) {
        ApiModelPropertyWrapper apiModelPropertyWrapper = new ApiModelPropertyWrapper(apiModelProperty);
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(field);
        String name = getFieldName(field, apiModelPropertyWrapper);
        String type = PluginUtil.getDataType(clazz);
        String description = apiModelPropertyWrapper.getDescription();
        boolean required = apiModelPropertyWrapper.getRequired();
        String example = apiModelPropertyWrapper.getExample();

        FieldDocInfo fieldDocInfo = new FieldDocInfo();
        fieldDocInfo.setName(name);
        fieldDocInfo.setType(type);
        fieldDocInfo.setRequired(getRequiredValue(required));
        fieldDocInfo.setExample(example);
        fieldDocInfo.setDescription(description);
        fieldDocInfo.setOrderIndex(apiModelPropertyWrapper.getPosition());

        if (PluginUtil.isCollection(clazz)) {
            Class<?> elementClass = this.getCollectionElementClass(clazz);
            if (elementClass != null) {
                clazz = elementClass;
            }
        }
        List<FieldDocInfo> children = buildFieldDocInfosByType(clazz, false);
        fieldDocInfo.setChildren(children);

        return fieldDocInfo;
    }

    private boolean isClassFieldHidden(Class<?> clazz, Field field) {
        String key = clazz.getName() + "." + field.getName();
        return hiddenColumns.contains(key);
    }

    private static byte getRequiredValue(boolean b) {
        return (byte) (b ? 1 : 0);
    }

    private static String getFieldType(Field field, ApiModelPropertyWrapper apiModelProperty) {
        String dataType = apiModelProperty.getDataType();
        if (StringUtils.hasText(dataType)) {
            return dataType;
        }
        Class<?> type = field.getType();
        String rawTypeName = type.getSimpleName();
        String multipartFile = "MultipartFile";
        if (rawTypeName.contains(multipartFile)) {
            return type.isArray() ? DataType.FILES.getValue() : DataType.FILE.getValue();
        }
        if (Collection.class.isAssignableFrom(type)) {
            Type genericType = field.getGenericType();
            return genericType.getTypeName().contains(multipartFile) ?
                    DataType.FILES.getValue() : 
                    DataType.ARRAY.getValue();
        }
        if (type.isArray()) {
            return DataType.ARRAY.getValue();
        }
        if (type == Date.class) {
            return DataType.DATE.getValue();
        }
        if (type == Timestamp.class) {
            return DataType.DATETIME.getValue();
        }
        return PluginUtil.isPojo(type) ? DataType.OBJECT.getValue() : field.getType().getSimpleName().toLowerCase();
    }

    private static String getFieldName(Field field, ApiModelPropertyWrapper apiModelPropertyWrapper) {
        String name = apiModelPropertyWrapper.getName();
        if (StringUtils.hasText(name)) {
            return name;
        }
        return field.getName();
    }

    private static String getFieldDescription(ApiModelProperty apiModelProperty) {
        if (apiModelProperty == null) {
            return "";
        }
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
