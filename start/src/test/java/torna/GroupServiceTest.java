package torna;

import torna.service.SpaceService;
import torna.service.dto.SpaceAddDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author tanghc
 */
public class GroupServiceTest extends DocsphereApplicationTests {

    @Autowired
    SpaceService spaceService;

    @Test
    public void testAdd() {
        SpaceAddDTO spaceAddDTO = new SpaceAddDTO();
        spaceAddDTO.setLeaderId(1L);
        spaceAddDTO.setName("测试" + System.currentTimeMillis());
        spaceAddDTO.setCreator("张三");
        spaceService.addSpace(spaceAddDTO);
    }

}
