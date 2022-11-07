package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class SpaceInfoDTO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 分组名称, 数据库字段：name */
    private String name;

    /**  数据库字段：creator_name */
    private String creatorName;

    private Byte isCompose;

    /** 空间管理员 */
    private List<UserInfoDTO> leaders;

    /**  数据库字段：gmt_create */
    private LocalDateTime gmtCreate;

}
