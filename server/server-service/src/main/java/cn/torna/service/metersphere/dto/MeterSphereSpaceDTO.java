package cn.torna.service.metersphere.dto;

import lombok.Data;

import java.util.List;

/**
 * @author thc
 */
@Data
public class MeterSphereSpaceDTO {

    private String id;

    private String name;

    private List<MeterSphereProjectDTO> projects;

}
