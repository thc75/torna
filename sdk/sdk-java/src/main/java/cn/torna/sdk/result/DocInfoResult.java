package cn.torna.sdk.result;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tanghc
 */
@Getter
@Setter
public class DocInfoResult {
    private String id;

    /** 文档名称 */
    private String name;

    /** 文档概述 */
    private String description;

    /** 访问URL */
    private String url;

    /** http方法 */
    private String httpMethod;

    /** contentType */
    private String contentType;

    /** 是否是分类，0：不是，1：是 */
    private Byte isFolder;

    /** 父节点 */
    private String parentId;

    /** 是否显示 */
    private Byte isShow;

}
