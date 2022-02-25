package cn.torna.service.dto;

import cn.torna.common.bean.TreeAware;
import lombok.Data;

import java.util.List;

/**
 * 备注：模块公共参数
 *
 * @author tanghc
 */
@Data
public class ModuleEnvironmentParamDTO implements TreeAware<ModuleEnvironmentParamDTO, Long> {

    private Long id;

    /** 
     * 唯一id，md5(doc_id:parent_id:style:name)
     */
    private String dataId;

    /** 
     * 字段名称
     */
    private String name;

    /** 
     * 字段类型
     */
    private String type;

    /** 
     * 是否必须，1：是，0：否
     */
    private Byte required;

    /** 
     * 最大长度
     */
    private String maxLength;

    /** 
     * 示例值
     */
    private String example;

    /** 
     * 描述
     */
    private String description;

    /** 
     * enum_info.id
     */
    private Long enumId;

    /** 
     * module_environment.id
     */
    private Long environmentId;

    private Long parentId;

    /** 
     * 0：path, 1：header， 2：请求参数，3：返回参数，4：错误码
     */
    private Byte style;

    /** 
     * 新增操作方式，0：人工操作，1：开放平台推送
     */
    private Byte createMode;

    /** 
     * 修改操作方式，0：人工操作，1：开放平台推送
     */
    private Byte modifyMode;

    private Long creatorId;

    private String creatorName;

    private Long modifierId;

    private String modifierName;

    /** 
     * 排序索引
     */
    private Integer orderIndex;

    private List<ModuleEnvironmentParamDTO> children;

}