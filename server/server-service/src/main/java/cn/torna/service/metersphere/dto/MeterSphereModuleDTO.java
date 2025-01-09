package cn.torna.service.metersphere.dto;

import lombok.Data;

import java.util.List;

/**
 * @author thc
 */
@Data
public class MeterSphereModuleDTO {

    private String id;

    private String name;

    private List<MeterSphereModuleDTO> children;

    private Integer level;

    private Boolean leaf;
}
