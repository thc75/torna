package cn.torna;

import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.mapper.DocInfoMapper;
import cn.torna.manager.tx.TornaTransactionManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class TornaTransactionManagerTest extends TornaApplicationTests{
    @Autowired
    TornaTransactionManager tornaTransactionManager;

    @Autowired
    private DocInfoMapper docInfoMapper;

    @Test
    public void test() {
        tornaTransactionManager.execute(() -> {
            DocInfo docInfo = docInfoMapper.getById(98);
            docInfo.setDescription(LocalDateTime.now().toString());
            docInfoMapper.update(docInfo);
            int i = 1 / 0;
            return null;
        }, e -> e.printStackTrace());
    }
}
