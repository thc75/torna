package cn.torna.web.controller.admin.param;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class SystemI18nConfigParam {

    /** 
     * 主键id
     */
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


    /** 
     * 配置项，json内容
     */
    private String content;



}