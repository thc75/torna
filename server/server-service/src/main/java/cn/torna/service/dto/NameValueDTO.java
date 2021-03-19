package cn.torna.service.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tanghc
 */
@Getter
@Setter
public class NameValueDTO {
    private String name;
    private String value;
    private Byte isDeleted = 0;

    @Override
    public String toString() {
        return name + '=' + value;
    }
}
