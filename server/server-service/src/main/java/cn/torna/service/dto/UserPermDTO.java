package cn.torna.service.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author tanghc
 */
@Data
public class UserPermDTO {

    // key: space:id value: dev
    private Map<String, String> roleData;
    private Byte isSuperAdmin;

}
