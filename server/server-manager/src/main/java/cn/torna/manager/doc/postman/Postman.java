package cn.torna.manager.doc.postman;


import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class Postman {
    private Info info;
    private List<Item> item;
}
