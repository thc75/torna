package cn.torna.dao.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tanghc
 */
@Getter
@Setter
public class ColumnInfo {
    /** 字段名 */
    private String name;
    /** 字段类型 */
    private String type;

    private String length;

}
