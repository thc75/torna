package cn.torna.service.metersphere.dto;

import lombok.Data;

import java.util.List;

/**
 * @author thc
 */
@Data
public class MeterSphereTestDTO {

    private Boolean success = false;

    private List<MeterSphereSpaceDTO> spaces;

}
