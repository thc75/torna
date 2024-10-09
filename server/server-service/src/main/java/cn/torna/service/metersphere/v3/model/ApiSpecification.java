package cn.torna.service.metersphere.v3.model;

import cn.torna.service.metersphere.v3.util.PropertiesLoader;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.torna.service.metersphere.v3.model.DefaultConstants.DEFAULT_PROPERTY_FILE;


/**
 * 对应文件
 */
@Data
public class ApiSpecification {

    /**
     * 严格模式: 未指定分类、接口名不处理
     */
    private boolean strict = false;

    /**
     * 路径前缀
     */
    private String path;

    /**
     * 返回值包装类
     */
    private String returnWrapType;

    /**
     * 返回值解包装类
     */
    private List<String> returnUnwrapTypes;

    /**
     * 参数忽略类
     */
    private List<String> parameterIgnoreTypes;

    /**
     * 自定义bean配置
     */
    private Map<String, BeanCustom> beans;

    /**
     * 自定义注解值，简化@RequestBody注解
     */
    private RequestBodyParamType requestBodyParamType;

    /**
     * 时间格式: 查询参数和表单
     */
    private String dateTimeFormatMvc;

    /**
     * 时间格式: Json
     */
    private String dateTimeFormatJson;

    /**
     * 日期格式
     */
    private String dateFormat;

    /**
     * 时间格式
     */
    private String timeFormat;

    private static final Pattern BEANS_PATTERN = Pattern.compile("^beans\\[(.+)]$");

    @Data
    public static class RequestBodyParamType {
        /**
         * 注解类型
         */
        private String annotation;

        /**
         * 注解属性
         */
        private String property;

        public RequestBodyParamType(String type) {
            String[] splits = type.split("#");
            this.annotation = splits[0];
            if (splits.length > 1) {
                this.property = splits[1];
            } else {
                this.property = "value";
            }
        }
    }

    /**
     * 解析配置
     */
    public static ApiSpecification fromProperties(Properties properties) {
        Splitter splitter = Splitter.on(",").trimResults().omitEmptyStrings();
        String strict = properties.getProperty("strict", "");
        String path = properties.getProperty("path", null);
        String returnWrapType = properties.getProperty("returnWrapType", "");
        String returnUnwrapTypes = properties.getProperty("returnUnwrapTypes", "");
        String parameterIgnoreTypes = properties.getProperty("parameterIgnoreTypes", "");
        String dateTimeFormatMvc = properties.getProperty("dateTimeFormatMvc", "");
        String dateTimeFormatJson = properties.getProperty("dateTimeFormatJson", "");
        String dateFormat = properties.getProperty("dateFormat", "");
        String timeFormat = properties.getProperty("timeFormat", "");
        String requestBodyParamType = properties.getProperty("requestBodyParamType", "");

        ApiSpecification config = new ApiSpecification();
        if (StringUtils.isNotEmpty(strict)) {
            config.strict = Boolean.parseBoolean(strict);
        }
        config.setPath(path);
        config.returnWrapType = returnWrapType.trim();
        config.returnUnwrapTypes = splitter.splitToList(returnUnwrapTypes);
        config.parameterIgnoreTypes = splitter.splitToList(parameterIgnoreTypes);
        config.dateTimeFormatMvc = dateTimeFormatMvc;
        config.dateTimeFormatJson = dateTimeFormatJson;
        config.dateFormat = dateFormat;
        config.timeFormat = timeFormat;
        if (StringUtils.isNotEmpty(requestBodyParamType)) {
            config.requestBodyParamType = new RequestBodyParamType(requestBodyParamType);
        }

        // 解析自定义bean配置: beans[xxx].json=xxx
        Gson gson = new Gson();
        Map<String, BeanCustom> beans = Maps.newHashMap();
        config.setBeans(beans);
        for (String p : properties.stringPropertyNames()) {
            String propertyValue = properties.getProperty(p);
            if (StringUtils.isEmpty(propertyValue)) {
                continue;
            }
            Matcher matcher = BEANS_PATTERN.matcher(p);
            if (!matcher.matches()) {
                continue;
            }
            String beanType = matcher.group(1);
            BeanCustom beanCustom = gson.fromJson(propertyValue, BeanCustom.class);
            beans.put(beanType, beanCustom);
        }

        return config;
    }

    /**
     * 合并配置
     */
    public static ApiSpecification getMergedInternalConfig(ApiSpecification settings) {
        Properties defaultProperties = PropertiesLoader.getProperties(DEFAULT_PROPERTY_FILE);
        ApiSpecification internal = ApiSpecification.fromProperties(defaultProperties);
        ApiSpecification config = new ApiSpecification();
        config.setStrict(settings.isStrict());
        config.setPath(settings.getPath());
        config.setReturnWrapType(settings.getReturnWrapType());
        config.setDateTimeFormatMvc(settings.getDateTimeFormatMvc());
        config.setDateTimeFormatJson(settings.getDateTimeFormatJson());
        config.setDateFormat(settings.getDateFormat());
        config.setTimeFormat(settings.getTimeFormat());
        config.setRequestBodyParamType(settings.getRequestBodyParamType());

        // 时间格式
        if (StringUtils.isBlank(settings.getDateTimeFormatMvc())) {
            config.setDateTimeFormatMvc(internal.getDateTimeFormatMvc());
        }
        if (StringUtils.isBlank(settings.getDateTimeFormatJson())) {
            config.setDateTimeFormatJson(internal.getDateTimeFormatJson());
        }
        if (StringUtils.isBlank(settings.getDateFormat())) {
            config.setDateFormat(internal.getDateFormat());
        }
        if (StringUtils.isBlank(settings.getTimeFormat())) {
            config.setTimeFormat(internal.getTimeFormat());
        }

        // 解包装类型
        List<String> returnUnwrapTypes = Lists.newArrayList();
        returnUnwrapTypes.addAll(internal.getReturnUnwrapTypes());
        if (settings.getReturnUnwrapTypes() != null) {
            returnUnwrapTypes.addAll(settings.getReturnUnwrapTypes());
        }
        config.setReturnUnwrapTypes(returnUnwrapTypes);

        // 忽略参数类型
        List<String> parameterIgnoreTypes = Lists.newArrayList();
        if (settings.getParameterIgnoreTypes() != null) {
            config.setReturnUnwrapTypes(returnUnwrapTypes);
            parameterIgnoreTypes.addAll(settings.getParameterIgnoreTypes());
        }
        parameterIgnoreTypes.addAll(internal.getParameterIgnoreTypes());
        config.setParameterIgnoreTypes(parameterIgnoreTypes);

        // 自定义bean配置
        Map<String, BeanCustom> beans = Maps.newHashMap();
        if (internal.getBeans() != null) {
            beans.putAll(internal.getBeans());
        }
        if (settings.getBeans() != null) {
            beans.putAll(settings.getBeans());
        }
        config.setBeans(beans);

        return config;
    }

    public BeanCustom getBeanCustomSettings(String type) {
        BeanCustom custom = null;
        if (this.beans != null) {
            custom = this.beans.get(type);
        }
        if (custom != null) {
            if (custom.getIncludes() == null) {
                custom.setIncludes(Collections.emptyNavigableSet());
            }
            if (custom.getExcludes() == null) {
                custom.setExcludes(Collections.emptyNavigableSet());
            }
            if (custom.getFields() == null) {
                custom.setFields(Maps.newHashMapWithExpectedSize(0));
            }
        }
        return custom;
    }

}
