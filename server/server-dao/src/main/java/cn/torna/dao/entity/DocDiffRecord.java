package cn.torna.dao.entity;

import java.time.LocalDateTime;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;

import lombok.Data;

/**
 * 表名：doc_diff_record
 * 备注：文档比较记录
 *
 * @author tanghc
 */
@Table(name = "doc_diff_record", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class DocDiffRecord {

    private Long id;


    /** 
     * doc_info.data_id
     */
    private String dataId;


    /** 
     * 旧MD5
     */
    private String md5Old;


    /** 
     * 新MD5
     */
    private String md5New;


    /** 
     * 修改方式，0：推送，1：表单编辑
     */
    private Byte modifySource;


    /** 
     * 修改人id
     */
    private Long modifyUserId;


    /** 
     * 修改人
     */
    private String modifyNickname;


    /** 
     * 变更类型，0：修改，1：新增，2：删除
     */
    private Byte modifyType;


    private LocalDateTime gmtCreate;


    private LocalDateTime gmtModified;



}