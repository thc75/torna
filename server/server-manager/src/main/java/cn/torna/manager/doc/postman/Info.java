package cn.torna.manager.doc.postman;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class Info {
    private String name;
    private String description;
    private String schema = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json";

}
