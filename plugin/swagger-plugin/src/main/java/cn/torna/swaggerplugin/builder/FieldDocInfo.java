package cn.torna.swaggerplugin.builder;

import lombok.Data;

import java.util.List;

/**
 * 文档参数字段信息
 * 
 * @author tanghc
 *
 */
@Data
public class FieldDocInfo {

    /** 字段名称 */
    private String name;

    /** 字段类型  */
    private String type;

    /** 是否必须，1：是，0：否 */
    private Byte required;

    /** 最大长度 */
    private String maxLength;

    /** 示例值 */
    private String example;

    /** 描述 */
    private String description;

    /** 父节点, 没有填空字符串 */
    private String parentId;

    /** 排序 */
    private Integer orderIndex;

    /** 子节点 */
    private List<FieldDocInfo> children;
}
