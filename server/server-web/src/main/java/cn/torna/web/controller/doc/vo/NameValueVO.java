package cn.torna.web.controller.doc.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NameValueVO {
    private String name;
    private String value;
    private Byte isDeleted = 0;
}
