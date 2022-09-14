package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.DocSnapshot;
import cn.torna.dao.mapper.DocSnapshotMapper;
import cn.torna.service.dto.DocInfoDTO;
import com.alibaba.fastjson.JSON;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tanghc
 */
@Service
public class DocSnapshotService extends BaseService<DocSnapshot, DocSnapshotMapper> {

    public DocSnapshot getByMd5(String md5) {
        Query query = new Query().eq("md5", md5)
                .orderby("id", Sort.DESC);
        return get(query);
    }

    public List<DocSnapshot> listDocSnapshotBaseInfo(long docId) {
        return this.getMapper().listDocSnapshotBaseInfo(docId);
    }

    /**
     * 存储快照
     * @param docInfoDTO 文档
     */
    @Async
    public void saveDocSnapshot(DocInfoDTO docInfoDTO) {
        String content = JSON.toJSONString(docInfoDTO);
        DocSnapshot docSnapshot = new DocSnapshot();
        docSnapshot.setMd5(docInfoDTO.getMd5());
        docSnapshot.setDocId(docInfoDTO.getId());
        docSnapshot.setModifierName(docInfoDTO.getModifierName());
        docSnapshot.setModifierTime(docInfoDTO.getGmtModified());
        docSnapshot.setContent(content);
        this.save(docSnapshot);
    }

}