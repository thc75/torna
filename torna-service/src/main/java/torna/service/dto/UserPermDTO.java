package torna.service.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author tanghc
 */
@Data
public class UserPermDTO {

    // key: space:id value:perms
    private Map<String, List<String>> permData;
    // key: space:id value: dev
    private Map<String, String> roleData;
    private Byte isAdmin;

}
