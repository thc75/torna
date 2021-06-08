package cn.torna.service.dto;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.User;
import cn.torna.common.enums.DocTypeEnum;
import cn.torna.common.support.IdCodec;
import cn.torna.service.dataid.DocInfoDataId;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Map;

/**
 * @author tanghc
 */
@Data
public class DocFolderCreateDTO implements DocInfoDataId {
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    private String name;

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long parentId;

    /** 维护人, 数据库字段：author */
    private String author;

    private Integer orderIndex;

    private DocTypeEnum docTypeEnum;

    private Map<String, ?> props;

    private User user;

    @Override
    public Byte getIsFolder() {
        return Booleans.TRUE;
    }

    @Override
    public String getUrl() {
        return "";
    }

    @Override
    public String getHttpMethod() {
        return "";
    }
}
