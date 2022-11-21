package cn.torna.swaggerplugin.builder;

import cn.torna.swaggerplugin.bean.ApiModelPropertyWrapper;
import cn.torna.swaggerplugin.bean.ApiParamInfo;
import cn.torna.swaggerplugin.bean.Booleans;
import cn.torna.swaggerplugin.bean.TornaConfig;
import cn.torna.swaggerplugin.util.ClassUtil;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.torna.swaggerplugin.util.PluginUtil.getGenericParamKey;

/**
 * 解析文档信息
 * @author tanghc
 */
public class ApiDocBuilder {

    // key: ClassA.getName() + fieldType.getName() + field.getName()
    private Map<String, AtomicInteger> cycleFieldsMap = new HashMap<>();
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

    private ApiParamInfo getApiParamInfo(Class<?> targetClass, String fieldName) {
        JSONObject jarClass = tornaConfig.getJarClass();
        if (jarClass == null) {
            return null;
        }
        List<Class<?>> allClasses = ClassUtil.getAllClasses(targetClass);
        for (Class<?> clazz : allClasses) {
            String className = clazz.getName();
            JSONObject classConfig = jarClass.getJSONObject(className);
            if (classConfig == null) {
                continue;
            }
            Set<String> fields = classConfig.keySet();
            for (String field : fields) {
                if (Objects.equals(field, fieldName)) {
                    JSONObject fieldConfig = classConfig.getJSONObject(field);
                    return fieldConfig == null ? null : fieldConfig.toJavaObject(ApiParamInfo.class);
                }
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
        Class<?> targetClassRef = PluginUtil.isCollectionOrArray(clazz) ? getCollectionElementClass(clazz) : clazz;

        // 查找泛型
        if(targetClassRef.getName().equals("org.springframework.http.ResponseEntity")){
            Field body = ReflectionUtils.findField(targetClassRef, "body");
            String typeName = ((TypeVariable<?>) body.getGenericType()).getName();
            targetClassRef = getGenericParamClass(targetClassRef, typeName);
            // 没有指定泛型类型
            if (targetClassRef == null) {
                System.out.println("Warning: Use ResponseEntity but not specified generic parameter.");
                return Collections.emptyList();
            }
        }

        final Class<?> targetClass = targetClassRef;
        // 如果是基本类型
        if (!PluginUtil.isPojo(targetClass) || isObjectClass(targetClass)) {
            return Collections.emptyList();
        }

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
            if (fieldType.isEnum()) {
                fieldDocInfo = buildFieldDocInfoByEnumClass((Class<Enum>) fieldType, apiModelProperty, field);
            } else if (realClass != null) {
                fieldDocInfo = buildFieldDocInfoByClass(apiModelProperty, realClass, field);
            } else if (PluginUtil.isPojo(fieldType)) {
                // 如果是自定义类
                fieldDocInfo = buildFieldDocInfoByClass(apiModelProperty, fieldType, field);
            } else {
                fieldDocInfo = buildFieldDocInfo(apiModelProperty, field);
            }
            formatDataType(fieldDocInfo, realClass != null ? realClass : fieldType);
            fieldDocInfos.add(fieldDocInfo);
        }, field -> {
            if (PluginUtil.isTransientField(field) || Modifier.isStatic(field.getModifiers()) || isClassFieldHidden(targetClass, field)) {
                return false;
            }
            ApiModelProperty apiModelProperty = AnnotationUtils.findAnnotation(field, ApiModelProperty.class);
            return apiModelProperty == null || !apiModelProperty.hidden();
        });
        this.bindJarClassFields(targetClass, fieldDocInfos);
        return fieldDocInfos;
    }

    protected void formatDataType(FieldDocInfo fieldDocInfo, Class<?> fieldType) {
        if (isObjectClass(fieldType)) {
            fieldDocInfo.setType(DataType.OBJECT.getValue());
            fieldDocInfo.setChildren(Collections.emptyList());
        }
    }

    protected boolean isObjectClass(Class<?> targetClass) {
        List<String> objectClassList = this.tornaConfig.getObjectClassList();
        if (objectClassList == null) {
            return false;
        }
        String name = targetClass.getName();
        return objectClassList.contains(name);
    }


    protected void bindJarClassFields(Class<?> targetClass, List<FieldDocInfo> fieldDocInfos) {
        for (FieldDocInfo child : fieldDocInfos) {
            ApiParamInfo apiParamInfo = this.getApiParamInfo(targetClass, child.getName());
            if (apiParamInfo != null) {
                child.setRequired(Booleans.toValue(apiParamInfo.isRequired()));
                child.setExample(apiParamInfo.getExample());
                child.setDescription(apiParamInfo.getValue());
                child.setOrderIndex(apiParamInfo.getPosition());
                if(!StringUtils.isEmpty(apiParamInfo.getName())){
                    child.setName(apiParamInfo.getName());
                }
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
        ApiModelPropertyWrapper apiModelPropertyWrapper = new ApiModelPropertyWrapper(apiModelProperty, field);
        Class<?> fieldType = field.getType();
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
        fieldDocInfo.setMaxLength(apiModelPropertyWrapper.getMaxLength());

        boolean isList = PluginUtil.isCollectionOrArray(fieldType);
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
                } else {
                    elementClass = Object.class;
                }
            }
        } else if (fieldType.isArray()) {
            elementClass = fieldType.getComponentType();
        }

        if (elementClass != null && elementClass != Object.class && elementClass != Void.class) {
            // 如果是嵌套泛型类型，如：List<List<String>>
            if (PluginUtil.isGenericType(elementClass)) {
                Type genericRawType = PluginUtil.getGenericRawType(elementClass);
                if (PluginUtil.isCollectionOrArray((Class<?>) genericRawType)) {
                    Type genericElType = PluginUtil.getGenericType(elementClass);
                    if (isList) {
                        Class<?> elType = (Class<?>) genericElType;
                        fieldDocInfo.setType("List<List<" + elType.getSimpleName() + ">>");
                    }
                }
            } else {
                Class<?> clazz = (Class<?>) elementClass;
                if (PluginUtil.isPojo(clazz)) {
                    List<FieldDocInfo> fieldDocInfos = isCycle(clazz, field)
                            ? Collections.emptyList()
                            : buildFieldDocInfosByType(clazz, false);
                    fieldDocInfo.setChildren(fieldDocInfos);
                }
            }
        }
        return fieldDocInfo;
    }

    protected Class<?> getGenericParamClass(Class<?> clazz, String name) {
        String genericParamKey = getGenericParamKey(clazz, name);
        return genericParamMap.get(genericParamKey);
    }

    protected FieldDocInfo buildFieldDocInfoByClass(ApiModelProperty apiModelProperty, Class<?> clazz, Field field) {
        ApiModelPropertyWrapper apiModelPropertyWrapper = new ApiModelPropertyWrapper(apiModelProperty, field);
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
        fieldDocInfo.setMaxLength(apiModelPropertyWrapper.getMaxLength());

        if (PluginUtil.isCollection(clazz)) {
            Class<?> elementClass = this.getCollectionElementClass(clazz);
            if (elementClass != null) {
                clazz = elementClass;
            }
        }
        // 解决循环依赖问题
        boolean cycle = isCycle(clazz, field);
        List<FieldDocInfo> children = cycle ? Collections.emptyList()
                : buildFieldDocInfosByType(clazz, false);
        fieldDocInfo.setChildren(children);

        return fieldDocInfo;
    }

    protected FieldDocInfo buildFieldDocInfoByEnumClass(Class<Enum> enumClass, ApiModelProperty apiModelProperty, Field field) {
        ApiModelPropertyWrapper apiModelPropertyWrapper = new ApiModelPropertyWrapper(apiModelProperty, field);
        Enum[] enumConstants = enumClass.getEnumConstants();
        FieldDocInfo fieldDocInfo = new FieldDocInfo();
        fieldDocInfo.setName(apiModelPropertyWrapper.getName());
        fieldDocInfo.setType(DataType.ENUM.getValue());
        fieldDocInfo.setRequired(Booleans.toValue(apiModelPropertyWrapper.getRequired()));
        fieldDocInfo.setOrderIndex(apiModelPropertyWrapper.getPosition());
        List<String> examples = new ArrayList<>(enumConstants.length);
        for (Enum enumConstant : enumConstants) {
            examples.add(enumConstant.name());
        }
        if (examples.size() > 0) {
            fieldDocInfo.setExample(examples.get(0));
        }
        fieldDocInfo.setDescription(String.join("、", examples));
        fieldDocInfo.setMaxLength(apiModelPropertyWrapper.getMaxLength());
        fieldDocInfo.setMaxLength(apiModelPropertyWrapper.getMaxLength());
        return fieldDocInfo;
    }

    protected boolean isCycle(Class<?> clazz, Field field) {
        String key = clazz.getName() + field.getType().getName() + field.getName();
        AtomicInteger atomicInteger = cycleFieldsMap.computeIfAbsent(key, k -> new AtomicInteger());
        return atomicInteger.getAndIncrement() >= 1;
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

    private void resetCycle() {
        cycleFieldsMap.clear();
    }
}
