package cn.torna.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class AllocateProjectDTO {

    private Long userId;
    
    private String role;

    private List<Long> projectIds;
    
}