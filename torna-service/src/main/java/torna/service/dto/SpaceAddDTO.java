package torna.service.dto;

import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class SpaceAddDTO {
    private String name;

    private List<Long> leaderIds;

    private Long creatorId;
}
