package cn.torna.manager.doc.postman;

import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class Request {

    private String method;
    private List<Header> header;
    private Url url;
    private Body body;
    private String description;

}
