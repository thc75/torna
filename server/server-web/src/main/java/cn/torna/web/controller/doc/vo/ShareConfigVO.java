package cn.torna.web.controller.doc.vo;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author tanghc
 */
@Data
public class ShareConfigVO {
    /**  数据库字段：id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 分享形式，1：公开，2：加密, 数据库字段：type */
    private Byte type;

    /** 密码, 数据库字段：password */
    private String password;

    /** 状态，1：有效，0：无效, 数据库字段：status */
    private Byte status;

    /** module.id, 数据库字段：module_id */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    /** 是否为全部文档, 数据库字段：is_all */
    private Byte isAll;

    /** 备注, 数据库字段：remark */
    private String remark;

    /**
     * 调试环境是否全选， 1-全选， 0-不选
     */
    private Byte isAllSelectedDebug;

    /** 是否显示调试 */
    private Byte isShowDebug;

    private String creatorName;

    private LocalDateTime gmtCreate;
}
