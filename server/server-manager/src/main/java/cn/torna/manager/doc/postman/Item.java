package cn.torna.manager.doc.postman;

import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class Item {

    /** 接口名称 */
    private String name;
    /** 接口详情 */
    private Request request;
    /** 如果是文件夹，下面会有子接口 */
    private List<Item> item;

    public boolean isFolder() {
        return item != null && item.size() > 0;
    }

}
