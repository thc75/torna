package cn.torna.manager.doc.postman;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author tanghc
 */
@Getter
@Setter
public class Request {

    private String method;
    private List<Header> header;
    private Url url;
    private Body body;
    private String description;

}
