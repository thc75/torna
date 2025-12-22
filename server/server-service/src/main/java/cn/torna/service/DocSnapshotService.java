package cn.torna.service;

import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.dao.entity.DocDiffDetail;
import cn.torna.dao.entity.DocDiffRecord;
import cn.torna.dao.entity.DocSnapshot;
import cn.torna.dao.mapper.DocDiffDetailMapper;
import cn.torna.dao.mapper.DocDiffRecordMapper;
import cn.torna.dao.mapper.DocSnapshotMapper;
import cn.torna.service.dto.DocInfoDTO;
import com.alibaba.fastjson.JSON;
import com.gitee.fastmybatis.core.query.LambdaQuery;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author tanghc
 */
@Service
@Slf4j
public class DocSnapshotService extends BaseLambdaService<DocSnapshot, DocSnapshotMapper> {

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private DocDiffRecordMapper docDiffRecordMapper;
    @Autowired
    private DocDiffDetailMapper docDiffDetailMapper;


    public DocSnapshot getByMd5(String md5) {
        if (StringUtils.isEmpty(md5)) {
            return null;
        }
        Query query = this.query()
                .eq(DocSnapshot::getMd5, md5)
                .orderBy(DocSnapshot::getId, Sort.DESC);
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
        DocSnapshot snapshot = getByField(DocSnapshot::getMd5, docInfoDTO.getMd5());
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

        try {
            removeSnapshotSize(docInfoDTO.getId(), EnvironmentKeys.TORNA_SNAPSHOT_SIZE.getInt());
        } catch (Exception e) {
            log.error("移除快照报错", e);
        }

        this.save(docSnapshot);
    }

    public void removeSnapshotSize(long docId, int limitSize) {
        Query query = this.query()
                .select(DocSnapshot::getId, DocSnapshot::getDocKey, DocSnapshot::getMd5)
                .eq(DocSnapshot::getDocId, docId)
                .orderByAsc(DocSnapshot::getId);
        List<DocSnapshot> list = this.list(query);
        if (list.isEmpty()) {
            return;
        }
        int size = list.size();

        // 删除快照
        if (size > limitSize) {
            int limit = size - limitSize;
            List<Long> removeIds = new ArrayList<>();
            Set<String> md5List = new HashSet<>();

            for (int i = 0; i < limit; i++) {
                DocSnapshot docSnapshot = list.get(i);

                removeIds.add(docSnapshot.getId());
                md5List.add(docSnapshot.getMd5());
            }

            this.getMapper().deleteByIds(removeIds);

            // 删除比较记录
            for (List<String> md5s : Lists.partition(new ArrayList<>(md5List), 50)) {
                Map<String, Long> md5NewIdMap = docDiffRecordMapper.query()
                        .in(DocDiffRecord::getMd5New, md5s)
                        .orderByAsc(DocDiffRecord::getId)
                        .map(DocDiffRecord::getMd5New, DocDiffRecord::getId, (v1, v2) -> v1);

                Map<String, Long> md5OldIdMap = docDiffRecordMapper.query()
                        .in(DocDiffRecord::getMd5Old, md5s)
                        .orderByAsc(DocDiffRecord::getId)
                        .map(DocDiffRecord::getMd5Old, DocDiffRecord::getId, (v1, v2) -> v1);

                Set<Long> recordIds = new HashSet<>(md5NewIdMap.values());

                recordIds.addAll(md5OldIdMap.values());

                removeRecord(recordIds);
            }
        }
    }

    private void removeRecord(Collection<Long> recordIds) {
        if (recordIds.isEmpty()) {
            return;
        }
        docDiffDetailMapper.deleteByIds(recordIds);

        docDiffDetailMapper.query()
                .in(DocDiffDetail::getRecordId, recordIds)
                .delete();
    }


    public void fillDocKey() {
        LambdaQuery<DocSnapshot> query = this.query()
                .select(DocSnapshot::getId, DocSnapshot::getDocId);
        List<DocSnapshot> docSnapshots = this.list(query);
        for (DocSnapshot docSnapshot : docSnapshots) {
            Long docId = docSnapshot.getDocId();
            String docKey = docInfoService.getDocKey(docId);
            if (StringUtils.hasText(docKey)) {
                this.query()
                        .set(DocSnapshot::getDocKey, docKey)
                        .eq(DocSnapshot::getId, docSnapshot.getId())
                        .update();
            }
        }
    }

}
