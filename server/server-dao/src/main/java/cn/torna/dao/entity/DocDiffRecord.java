package cn.torna.dao.entity;

import java.util.Date;

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
     * 操作key，雪花id
     */
    private Long operateKey;

    /**
     * doc_info.data_id
     */
    private String dataId;

    /**
     * 变更位置，0：文档名称，1：文档描述，2：contentType，3：deprecated，10：参数名称，11：参数类型，12：参数必填，13：参数最大长度，14：参数描述，15：参数示例值
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

    private Date gmtCreate;

    private Date gmtModified;


}