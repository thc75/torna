package torna.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import torna.common.bean.Booleans;
import torna.common.bean.User;
import torna.common.support.IdCodec;
import torna.service.dataid.DocInfoDataId;

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
