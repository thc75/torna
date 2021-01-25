package cn.torna.service.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class RightDTO {
    private List<RoleDTO> roles;

    public RightDTO() {
    }

    public RightDTO(List<RoleDTO> roles) {
        this.roles = roles;
    }

    public void addAll(RightDTO rightDTO) {
        if (this == rightDTO) {
            throw new IllegalArgumentException("操作失败");
        }
        if (this.roles == null) {
            this.roles = new ArrayList<>(8);
        }
        this.roles.addAll(rightDTO.roles);
    }
}
