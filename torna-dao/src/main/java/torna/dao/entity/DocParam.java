package torna.dao.entity;

import java.util.Date;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表名：doc_param
 * 备注：文档参数
 *
 * @author tanghc
 */
@Table(name = "doc_param")
@Data
public class DocParam {

	/**  数据库字段：id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
	/** 唯一id,md5(name:doc_id:parent_id), 数据库字段：unique_id */
    private String uniqueId;
    
	/** 字段名称, 数据库字段：name */
    private String name;
    
	/** 字段类型, 数据库字段：type */
    private String type;
    
	/** 是否必须，1：是，0：否, 数据库字段：required */
    private Byte required;
    
	/** 最大长度, 数据库字段：max_length */
    private String maxLength;
    
	/** 示例值, 数据库字段：example */
    private String example;
    
	/** 描述, 数据库字段：description */
    private String description;
    
	/** 参数枚举值,json数组格式，如：[{"code":"0",type:"string","msg":"已支付"}], 数据库字段：enum_content */
    private String enumContent;
    
	/** doc_info.id, 数据库字段：doc_id */
    private Long docId;
    
	/**  数据库字段：parent_id */
    private Long parentId;
    
	/** 0：header, 1：请求参数，2：返回参数，3：错误码, 数据库字段：style */
    private Byte style;
    
	/**  数据库字段：creator_id */
    private Long creatorId;
    
	/**  数据库字段：modifier_id */
    private Long modifierId;
    
	/**  数据库字段：is_deleted */
    @com.gitee.fastmybatis.core.annotation.LogicDelete
    private Byte isDeleted;
    
	/**  数据库字段：gmt_create */
    private Date gmtCreate;
    
	/**  数据库字段：gmt_modified */
    private Date gmtModified;
    

}