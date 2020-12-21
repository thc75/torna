package torna.service;

import com.alibaba.fastjson.JSON;
import torna.common.support.BaseService;
import torna.dao.entity.DocSnapshot;
import torna.dao.mapper.DocSnapshotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        DocSnapshot docSnapshot = new DocSnapshot();
        docSnapshot.setDocId(docInfoDTO.getId());
        docSnapshot.setModifierName(docInfoDTO.getModifierName());
        docSnapshot.setModifierTime(docInfoDTO.getGmtModified());
        docSnapshot.setContent(JSON.toJSONString(docInfoDTO));
        this.save(docSnapshot);
    }

}