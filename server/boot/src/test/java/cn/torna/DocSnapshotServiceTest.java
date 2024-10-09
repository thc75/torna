package cn.torna;

import cn.torna.service.DocSnapshotService;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * @author 六如
 */
public class DocSnapshotServiceTest extends TornaApplicationTests{

    @Resource
    DocSnapshotService docSnapshotService;

    @Test
    public void removeSnapshotSize() {
        docSnapshotService.removeSnapshotSize(131, 3);
    }
}
