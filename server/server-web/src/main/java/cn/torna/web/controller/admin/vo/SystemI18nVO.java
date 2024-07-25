package cn.torna.web.controller.admin.vo;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class SystemI18nVO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;


    /**
     * 语言简写，如:zh,en
     */
    private String lang;


    /**
     * 描述，如：简体中文
     */
    private String description;


    private String content;


}
