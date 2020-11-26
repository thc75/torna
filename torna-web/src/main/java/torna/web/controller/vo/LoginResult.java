package torna.web.controller.vo;

import lombok.Data;
import torna.service.dto.UserPermDTO;

/**
 * @author tanghc
 */
@Data
public class LoginResult {
    private String token;
    private UserPermDTO userPerm;
}
