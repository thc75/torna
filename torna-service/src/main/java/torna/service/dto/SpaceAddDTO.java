package torna.service.dto;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class SpaceAddDTO {
    private String name;

    private Long leaderId;

    private Long creatorId;

    private String creator;
}
