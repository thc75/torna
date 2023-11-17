package cn.torna.service.listener;

import cn.torna.common.bean.User;
import cn.torna.common.bean.UserCacheManager;
import cn.torna.common.enums.DocTypeEnum;
import cn.torna.service.DocDiffContext;
import cn.torna.service.DocInfoService;
import cn.torna.service.DocSnapshotService;
import cn.torna.service.dto.DocDiffDTO;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.event.DocAddEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author thc
 */
@Component
public class DocAddListener extends DefaultDocAddListener {

    @Autowired
    private DocSnapshotService docSnapshotService;

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private UserCacheManager userCacheManager;

    @Override
    public void onApplicationEvent(DocAddEvent event) {
        // 1. 添加快照
        Long docId = event.getDocId();
        DocInfoDTO docInfoDTO = docInfoService.getDocDetail(docId);
        // 自定义文档不参与
        if (docInfoDTO.getType() == DocTypeEnum.CUSTOM.getType()) {
            return;
        }
        docSnapshotService.saveDocSnapshot(docInfoDTO);
        // 2. 创建对比记录
        Long creatorId = docInfoDTO.getCreatorId();
        User user = userCacheManager.getUser(creatorId);
        DocDiffDTO docDiffDTO = new DocDiffDTO(null, docInfoDTO.getMd5(), LocalDateTime.now(), user, event.getSourceFromEnum());
        DocDiffContext.addQueue(docDiffDTO);
    }
}
