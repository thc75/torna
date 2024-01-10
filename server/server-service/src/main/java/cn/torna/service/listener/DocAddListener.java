package cn.torna.service.listener;

import cn.torna.common.bean.User;
import cn.torna.common.bean.UserCacheManager;
import cn.torna.common.enums.DocTypeEnum;
import cn.torna.dao.entity.DocInfo;
import cn.torna.service.DocDiffContext;
import cn.torna.service.DocInfoService;
import cn.torna.service.DocSnapshotService;
import cn.torna.service.dto.DocDiffDTO;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.event.DocAddEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Long docId = event.getDocId();
        // 1. 添加快照
        DocInfo docInfo = docInfoService.getById(docId);
        DocInfoDTO docInfoDTO = docInfoService.getDocDetail(docInfo.getId());
        docSnapshotService.saveDocSnapshot(docInfoDTO);

        DocInfo old = getOld(docInfo);
        String md5Old = Optional.ofNullable(old).map(DocInfo::getMd5).orElse(null);
        // 2. 创建对比记录
        Long creatorId = docInfoDTO.getCreatorId();
        User user = userCacheManager.getUser(creatorId);
        DocDiffDTO docDiffDTO = new DocDiffDTO(md5Old, docInfoDTO.getMd5(), LocalDateTime.now(), user, event.getSourceFromEnum());
        DocDiffContext.addQueue(docDiffDTO);
    }

    private DocInfo getOld(DocInfo docInfo) {
        List<DocInfo> docInfos = docInfoService.listByDocKey(docInfo.getDocKey());
        if (docInfos.isEmpty()) {
            return null;
        }
        // 获取最后一个不是自己
        return docInfos.stream()
                .filter(data -> !Objects.equals(data.getDataId(), docInfo.getDataId()))
                .max(Comparator.comparing(DocInfo::getGmtCreate))
                .orElse(null);

    }

}
