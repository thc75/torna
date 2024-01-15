package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.DocDiffRecord;
import cn.torna.dao.entity.DocSnapshot;
import cn.torna.dao.mapper.DocSnapshotMapper;
import cn.torna.service.dto.DocInfoDTO;
import com.alibaba.fastjson.JSON;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author tanghc
 */
@Service
public class DocSnapshotService extends BaseService<DocSnapshot, DocSnapshotMapper> {

    @Autowired
    private DocInfoService docInfoService;

    public DocSnapshot getByMd5(String md5) {
        if (StringUtils.isEmpty(md5)) {
            return null;
        }
        Query query = new Query().eq("md5", md5)
                .orderby("id", Sort.DESC);
        return get(query);
    }

    public List<DocSnapshot> listDocSnapshotBaseInfo(long docId) {
        return this.getMapper().listDocSnapshotBaseInfo(docId);
    }

    /**
     * 存储快照
     *
     * @param docInfoDTO 文档
     */
    public void saveDocSnapshot(DocInfoDTO docInfoDTO) {
        DocSnapshot snapshot = get("md5", docInfoDTO.getMd5());
        if (snapshot != null) {
            return;
        }
        String content = JSON.toJSONString(docInfoDTO);
        DocSnapshot docSnapshot = new DocSnapshot();
        docSnapshot.setMd5(docInfoDTO.getMd5());
        docSnapshot.setDocId(docInfoDTO.getId());
        docSnapshot.setModifierName(docInfoDTO.getModifierName());
        docSnapshot.setModifierTime(Optional.ofNullable(docInfoDTO.getGmtModified()).orElseGet(LocalDateTime::now));
        docSnapshot.setContent(content);
        this.save(docSnapshot);
    }

    public void fillDocKey() {
        List<DocSnapshot> list = this.list(new Query());
        for (DocSnapshot docSnapshot : list) {
            Long docId = docSnapshot.getDocId();
            String docKey =  docInfoService.getDocKey(docId);
            if (StringUtils.hasText(docKey)) {
                docSnapshot.setDocKey(docKey);
                this.update(docSnapshot);
            }
        }
    }

}
