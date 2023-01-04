package cn.torna.service.listener;

import cn.torna.common.bean.User;
import cn.torna.common.bean.UserCacheManager;
import cn.torna.common.enums.DocTypeEnum;
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

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
            // 自定义文档不参与
            if (docInfoDTO.getType() == DocTypeEnum.CUSTOM.getType()) {
                return;
            }
            docSnapshotService.saveDocSnapshot(docInfoDTO);

            // 2. 创建对比记录
            String oldMd5 = event.getOldMd5();
            String newMd5 = docInfoDTO.getMd5();
            if (Objects.equals(oldMd5, newMd5)) {
                return;
            }
            Long modifierId = docInfoDTO.getModifierId();
            User user = userCacheManager.getUser(modifierId);
            DocDiffDTO docDiffDTO = new DocDiffDTO(oldMd5, newMd5, user, event.getSourceFromEnum());
            DocDiffContext.addQueue(docDiffDTO);
        }
    }
}
