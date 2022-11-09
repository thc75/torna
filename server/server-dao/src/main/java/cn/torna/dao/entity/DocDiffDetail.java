package cn.torna.dao.entity;

import java.time.LocalDateTime;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;

import lombok.Data;

/**
 * 表名：doc_diff_detail
 * 备注：文档比较详情
 *
 * @author tanghc
 */
@Table(name = "doc_diff_detail", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class DocDiffDetail {

    private Long id;


    /**
     * doc_diff_record.id
     */
    private Long recordId;


    /**
     * 变更位置，0：文档名称，1：文档描述，2：contentType，3：httpMethod，10：参数名称，11：参数类型，12：参数必填，13：参数最大长度，14：参数描述，15：参数示例值
     */
    private Byte positionType;


    /**
     * 目标名称
     */
    private String targetName;


    /**
     * 旧值
     */
    private String oldValue;


    /**
     * 新值
     */
    private String newValue;


    /**
     * 变更类型，0：修改，1：新增，2：删除
     */
    private Byte modifyType;


    /**
     * 修改人
     */
    private String modifyNickname;


    private LocalDateTime gmtCreate;


    private LocalDateTime gmtModified;



}