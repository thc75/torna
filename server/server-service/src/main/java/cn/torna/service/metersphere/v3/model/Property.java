package cn.torna.service.metersphere.v3.model;

import cn.torna.service.metersphere.v3.constants.ParameterIn;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 参数
 */
@Data
public class Property {

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 时间格式
     */
    private String dateFormat;

    /**
     * 描述
     */
    private String description;

    /**
     * 参数位置
     */
    private ParameterIn in;

    /**
     * 是否必须
     */
    private Boolean required;

    /**
     * 是否标记过期
     */
    private Boolean deprecated;

    /**
     * 请求示例
     */
    private String example;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 值列表
     */
    private List<Value> values;

    /**
     * 当type为array
     */
    private Property items;

    /**
     * 当type为array, item元素是否唯一
     */
    private Boolean uniqueItems;

    /**
     * 最小元素个数
     */
    @SerializedName(value = "minLength", alternate = "minItems")
    private Integer minLength;

    /**
     * 最大元素个数
     */
    @SerializedName(value = "maxLength", alternate = "maxItems")
    private Integer maxLength;

    /**
     * 当type为object
     */
    private Map<String, Property> properties;

    /**
     * 最小值
     */
    private BigDecimal minimum = null;

    /**
     * 最大值
     */
    private BigDecimal maximum = null;

    public boolean isArrayType() {
        return DataTypes.ARRAY.equals(type);
    }

    public boolean isObjectType() {
        return DataTypes.OBJECT.equals(type);
    }

    public boolean isStringType() {
        return DataTypes.STRING.equals(type);
    }

    public boolean isNumberOrIntegerType() {
        return DataTypes.NUMBER.equals(type) || DataTypes.INTEGER.equals(type);
    }

    public boolean isFileType() {
        return DataTypes.FILE.equals(type);
    }


    /**
     * 合并自定义配置, 自定义优先
     */
    public void mergeCustom(Property custom) {
        if (StringUtils.isNotEmpty(custom.getName())) {
            this.name = custom.getName();
        }
        if (StringUtils.isNotEmpty(custom.getType())) {
            this.type = custom.getType();
        }
        if (StringUtils.isNotEmpty(custom.getDescription())) {
            this.description = custom.getDescription();
        }
        if (custom.getRequired() != null) {
            this.required = custom.getRequired();
        }
        if (custom.getDeprecated() != null) {
            this.deprecated = custom.getDeprecated();
        }
        if (StringUtils.isNotEmpty(custom.getDefaultValue())) {
            this.defaultValue = custom.getDefaultValue();
        }
        if (StringUtils.isNotEmpty(custom.getExample())) {
            this.example = custom.getExample();
        }
        if (custom.getMaxLength() != null) {
            this.maxLength = custom.getMaxLength();
        }
        if (custom.getMinLength() != null) {
            this.minLength = custom.getMinLength();
        }
        if (custom.getUniqueItems() != null) {
            this.uniqueItems = custom.getUniqueItems();
        }
    }

    public List<Value> getPropertyValues() {
        Property item = this;
        List<Value> values = item.getValues();
        boolean isArrayEnum = DataTypes.ARRAY.equals(item.getType())
                && item.getItems() != null
                && CollectionUtils.isNotEmpty(item.getItems().getValues());
        if (isArrayEnum) {
            values = item.getItems().getValues();
        }
        return values;
    }

    public void addProperty(String key, Property value) {
        if (this.properties == null) {
            this.properties = new LinkedHashMap<>();
        }
        this.properties.put(key, value);
    }

}
