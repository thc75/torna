package cn.torna.common.bean;

import lombok.Data;

@Data
public class DocChangeContext {

    private Long docId;

    private String md5Old;

    private String md5New;

}
