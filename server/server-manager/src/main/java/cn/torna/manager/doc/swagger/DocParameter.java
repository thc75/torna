package cn.torna.manager.doc.swagger;

import cn.torna.manager.doc.IParam;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 参数	类型	是否必填	最大长度	描述	示例值
 * @author tanghc
 */
public class DocParameter implements IParam {

    private static final AtomicInteger idGen = new AtomicInteger();

    private Integer id = idGen.incrementAndGet();
    private String module;
    /**
     * path,header,formData
     */
    private String in;
    private String name;
    private String type;
    private String parentType;
    private String maxLength = "-";
    private boolean required;
    private String description;
    private String example;
    @JSONField(name = "x-example")
    private String exampleX;
    private List<String> enums;
    private List<DocParameter> refs;
    /**
     * 数组元素类型
     */
    private String elementType;

    @Override
    public <T extends IParam> List<T> getChildren() {
        return (List<T>) getRefs();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getExampleX() {
        return exampleX;
    }

    public void setExampleX(String exampleX) {
        this.exampleX = exampleX;
        this.example = exampleX;
    }

    public List<DocParameter> getRefs() {
        return refs;
    }

    public void setRefs(List<DocParameter> refs) {
        this.refs = refs;
    }

    public List<String> getEnums() {
        return enums;
    }

    public void setEnums(List<String> enums) {
        this.enums = enums;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    @Override
    public String toString() {
        return "DocParameter{" +
                "id=" + id +
                ", module='" + module + '\'' +
                ", in='" + in + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", maxLength='" + maxLength + '\'' +
                ", required=" + required +
                ", description='" + description + '\'' +
                ", example='" + example + '\'' +
                ", exampleX='" + exampleX + '\'' +
                ", enums=" + enums +
                ", refs=" + refs +
                ", elementType='" + elementType + '\'' +
                '}';
    }
}
