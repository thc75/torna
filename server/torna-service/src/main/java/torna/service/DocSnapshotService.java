package torna.service;

import org.springframework.stereotype.Service;
import torna.common.support.BaseService;
import torna.common.util.FastjsonUtil;
import torna.dao.entity.DocSnapshot;
import torna.dao.mapper.DocSnapshotMapper;
import torna.service.dto.DocInfoDTO;

import java.util.List;

/**
 * @author tanghc
 */
@Service
public class DocSnapshotService extends BaseService<DocSnapshot, DocSnapshotMapper> {

    public List<DocSnapshot> listDocSnapshotBaseInfo(long docId) {
        return this.getMapper().listDocSnapshotBaseInfo(docId);
    }

    /**
     * 存储快照
     * @param docInfoDTO 文档
     */
    public void saveDocSnapshot(DocInfoDTO docInfoDTO) {
        String content = FastjsonUtil.toJSONString(docInfoDTO);
        DocSnapshot docSnapshot = new DocSnapshot();
        docSnapshot.setDocId(docInfoDTO.getId());
        docSnapshot.setModifierName(docInfoDTO.getModifierName());
        docSnapshot.setModifierTime(docInfoDTO.getGmtModified());
        docSnapshot.setContent(content);
        this.save(docSnapshot);
    }

}