package torna.web.controller.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author tanghc
 */
@Data
public class SpaceVO {

    private Long id;

    /** 分组名称, 数据库字段：name */
    private String name;

    /** 创建人, 数据库字段：creator */
    private String creator;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

}
