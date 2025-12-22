package cn.torna.manager.doc.postman;

import lombok.Data;

import java.util.List;

@Data
public class Response {
    private String name;
    private Request originalRequest;
    private String status;
    private Integer code;

    private List<Header> header;
    private String _postman_previewlanguage;
    private String body;
}
