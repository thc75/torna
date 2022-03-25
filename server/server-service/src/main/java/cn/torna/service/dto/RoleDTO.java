package cn.torna.service.dto;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class RoleDTO {
    private String prefix;
    private Long id;
    private String role;

    public RoleDTO(String prefix, Long id, String role) {
        this.prefix = prefix;
        this.id = id;
        this.role = role;
    }
}
