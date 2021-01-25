package cn.torna.service.dto;

import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class PermDTO {
    private String prefix;
    private Long id;
    private List<String> perms;

    public PermDTO(String prefix, Long id, List<String> perms) {
        this.prefix = prefix;
        this.id = id;
        this.perms = perms;
    }
}
