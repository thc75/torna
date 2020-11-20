package torna;

import torna.service.SpaceService;
import torna.service.dto.SpaceAddDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * @author tanghc
 */
public class GroupServiceTest extends DocsphereApplicationTests {

    @Autowired
    SpaceService spaceService;

    @Test
    public void testAdd() {
        SpaceAddDTO spaceAddDTO = new SpaceAddDTO();
        spaceAddDTO.setLeaderIds(Arrays.asList(1L));
        spaceAddDTO.setName("测试" + System.currentTimeMillis());
        spaceService.addSpace(spaceAddDTO);
    }

}
