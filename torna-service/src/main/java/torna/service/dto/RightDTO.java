package torna.service.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class RightDTO {
    private List<PermDTO> perms;
    private List<RoleDTO> roles;

    public RightDTO() {
    }

    public RightDTO(List<PermDTO> perms, List<RoleDTO> roles) {
        this.perms = perms;
        this.roles = roles;
    }

    public void addAll(RightDTO rightDTO) {
        if (this == rightDTO) {
            throw new IllegalArgumentException("操作失败");
        }
        if (this.perms == null) {
            this.perms = new ArrayList<>(8);
        }
        if (this.roles == null) {
            this.roles = new ArrayList<>(8);
        }
        this.perms.addAll(rightDTO.perms);
        this.roles.addAll(rightDTO.roles);
    }
}
