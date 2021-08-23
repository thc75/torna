package cn.torna.service.dto;

import lombok.Data;

/**
 * @author thc
 */
@Data
public class DocMeta {
    private String dataId;
    private String md5;
    private Byte isLocked;
}
