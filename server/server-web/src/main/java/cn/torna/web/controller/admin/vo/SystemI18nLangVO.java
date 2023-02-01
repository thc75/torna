package cn.torna.web.controller.admin.vo;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class SystemI18nLangVO {


    /**
     * 语言简写，如:zh,en
     */
    private String lang;


    /**
     * 描述，如：简体中文
     */
    private String description;


}