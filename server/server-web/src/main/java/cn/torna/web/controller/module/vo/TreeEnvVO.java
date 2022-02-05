package cn.torna.web.controller.module.vo;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class TreeEnvVO {

    private String id;

    private String label;

    private String parentId;

    /**
     * module_environment.id
     */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long envId;

    private String url;

    private Byte isModule;

    private Byte isEnv;


    public TreeEnvVO(String id, String label, String parentId) {
        this.id = id;
        this.label = label;
        this.parentId = parentId;
    }

}
