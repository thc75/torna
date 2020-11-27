package torna;

import torna.service.DocInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author tanghc
 */
public class DocInfoServiceTest extends TornaApplicationTests {

    @Autowired
    private DocInfoService docInfoService;

    @Test
    public void get() {
        docInfoService.getById(1L);
    }
}
