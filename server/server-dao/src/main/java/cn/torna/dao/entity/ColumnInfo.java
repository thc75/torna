package cn.torna.dao.entity;

/**
 * @author tanghc
 */
public class ColumnInfo {
    /** 字段名 */
    private String name;
    /** 字段类型 */
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
