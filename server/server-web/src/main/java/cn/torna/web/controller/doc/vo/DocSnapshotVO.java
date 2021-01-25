package cn.torna.web.controller.doc.vo;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * 表名：doc_snapshoot
 * 备注：文档快照
 *
 * @author tanghc
 */
@Data
public class DocSnapshotVO {

    /**  数据库字段：id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 修改人, 数据库字段：modifier_name */
    private String modifierName;

    /** 修改时间, 数据库字段：modifier_time */
    private Date modifierTime;

    /** 修改内容, 数据库字段：content */
    private String content;

}