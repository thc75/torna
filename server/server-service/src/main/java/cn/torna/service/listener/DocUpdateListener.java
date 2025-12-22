package cn.torna.service.listener;

import cn.torna.common.bean.UserCacheManager;
import cn.torna.service.DocDiffContext;
import cn.torna.service.DocInfoService;
import cn.torna.service.DocSnapshotService;
import cn.torna.service.dto.DocDiffDTO;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.event.DocUpdateEvent;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author thc
 */
@Component
public class DocUpdateListener extends DefaultDocUpdateListener {

    private Interner<String> interner = Interners.newWeakInterner();

    @Autowired
    private DocSnapshotService docSnapshotService;

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private UserCacheManager userCacheManager;

    @Override
    public void onApplicationEvent(DocUpdateEvent event) {
        synchronized (interner.intern(String.valueOf(event.getDocId()))) {
            // 1. 先保存快照
            DocInfoDTO docInfoDTO = docInfoService.getDocDetail(event.getDocId());
            docSnapshotService.saveDocSnapshot(docInfoDTO);

            // 2. 创建对比记录
            String oldMd5 = event.getOldMd5();
            DocDiffDTO docDiffDTO = new DocDiffDTO(event.getDocId(), oldMd5, null, LocalDateTime.now(), null, event.getSourceFromEnum());
            DocDiffContext.addQueue(docDiffDTO);
        }
    }
}
