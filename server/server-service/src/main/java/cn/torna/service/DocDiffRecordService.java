package cn.torna.service;
import com.alibaba.fastjson.JSONObject;

import cn.torna.common.bean.User;
import cn.torna.common.enums.DocDiffModifySourceEnum;
import cn.torna.common.enums.ModifyType;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.DocDiffDetail;
import cn.torna.dao.entity.DocDiffRecord;
import cn.torna.dao.entity.DocSnapshot;
import cn.torna.dao.mapper.DocDiffRecordMapper;
import cn.torna.service.dto.DocDiffDTO;
import cn.torna.service.dto.DocDiffDetailDTO;
import cn.torna.service.dto.DocDiffRecordDTO;
import cn.torna.service.dto.DocInfoDTO;
import com.alibaba.fastjson.JSON;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * @author tanghc
 */
@Service
@Slf4j
public class DocDiffRecordService extends BaseService<DocDiffRecord, DocDiffRecordMapper> {

    @Autowired
    private DocSnapshotService docSnapshotService;

    @Autowired
    private DocDiffDetailService docDiffDetailService;

    // TODO：获取修改记录
    /**
     * 获取修改记录
     * @param docId 文档id
     * @return
     */
    public List<DocDiffRecordDTO> listDocDiff(Long docId) {
        List<DocDiffRecord> diffRecordList = list("doc_id", docId);
        if (CollectionUtils.isEmpty(diffRecordList)) {
            return Collections.emptyList();
        }
        List<Long> ids = diffRecordList.stream().map(DocDiffRecord::getId).collect(Collectors.toList());
        List<DocDiffDetail> docDiffDetails = docDiffDetailService.listByCollection("record_id", ids);
        // KEY:recordId
        Map<Long, List<DocDiffDetail>> recordDetailMap = docDiffDetails.stream()
                .collect(Collectors.groupingBy(DocDiffDetail::getRecordId));
        diffRecordList.sort(Comparator.comparing(DocDiffRecord::getGmtCreate, Comparator.reverseOrder()));

        return diffRecordList.stream()
                .map(docDiffRecord -> {
                    DocDiffRecordDTO docDiffRecordDTO = CopyUtil.copyBean(docDiffRecord, DocDiffRecordDTO::new);
                    List<DocDiffDetail> docDiffDetailList = recordDetailMap.get(docDiffRecord.getId());
                    List<DocDiffDetailDTO> details = buildDocDiffDetails(docDiffDetailList);
                    docDiffRecordDTO.setDocDiffDetails(details);
                    return docDiffRecordDTO;
                })
                .collect(Collectors.toList());
    }

    private List<DocDiffDetailDTO> buildDocDiffDetails(List<DocDiffDetail> docDiffDetailList) {
        if (CollectionUtils.isEmpty(docDiffDetailList)) {
            return Collections.emptyList();
        }
        return docDiffDetailList.stream()
                .map(docDiffDetail -> {
                    DocDiffDetailDTO docDiffDetailDTO = new DocDiffDetailDTO();
                    docDiffDetailDTO.setModifyType(docDiffDetail.getModifyType());
                    docDiffDetailDTO.setId(docDiffDetail.getId());
                    docDiffDetailDTO.setPositionType(docDiffDetail.getPositionType());
                    docDiffDetailDTO.setTargetName(docDiffDetail.getTargetName());
                    docDiffDetailDTO.setContent(JSON.parseObject(docDiffDetail.getContent()));
                    return docDiffDetailDTO;
                })
                .collect(Collectors.toList());
    }


    public void doDocDiff(String oldMd5, DocInfoDTO docInfoDTO, DocDiffModifySourceEnum sourceEnum, User user) {
        doDocDiffNow(oldMd5, docInfoDTO, sourceEnum, user, DocDiffContext::addQueue);
    }

    public boolean existRecord(String oldMd5, String newMd5) {
        Query query = new Query()
                .eq("md5_old", oldMd5)
                .eq("md5_new", newMd5);
        return this.get(query) != null;
    }

    public void doDocDiffNow(String oldMd5, DocInfoDTO docInfoDTO, DocDiffModifySourceEnum sourceEnum, User user, Consumer<DocDiffDTO> consumer) {
        String newMd5 = docInfoDTO.getMd5();
        boolean contentChanged = !Objects.equals(oldMd5, newMd5);
        // 文档内容被修改，做相关处理
        if (contentChanged) {
            // 保存新md5内容
            docSnapshotService.saveDocSnapshot(docInfoDTO);
            consumer.accept(new DocDiffDTO(oldMd5, newMd5, user, sourceEnum));
        }
    }

    public void processDocDiff(DocDiffDTO docDiffDTO) {
        String md5New = docDiffDTO.getMd5New();
        String md5Old = docDiffDTO.getMd5Old();
        if (existRecord(md5Old, md5New)) {
            log.debug("变更记录已存在, md5Old={}, md5New={}", md5Old, md5New);
            return;
        }
        DocSnapshot snapshotOld = docSnapshotService.getByMd5(md5Old);
        DocSnapshot snapshotNew = docSnapshotService.getByMd5(md5New);

        // del
        if (snapshotOld != null && snapshotNew == null) {
            DocInfoDTO docInfoOld = JSON.parseObject(snapshotOld.getContent(), DocInfoDTO.class);
            this.createRecord(docInfoOld, docDiffDTO, ModifyType.DELETE);
        } else if (snapshotOld == null && snapshotNew != null) {
            // new
            DocInfoDTO docInfoNew = JSON.parseObject(snapshotNew.getContent(), DocInfoDTO.class);
            this.createRecord(docInfoNew, docDiffDTO, ModifyType.ADD);
        } else if (snapshotOld != null && snapshotNew != null) {
            // update
            DocInfoDTO docInfoOld = JSON.parseObject(snapshotOld.getContent(), DocInfoDTO.class);
            DocInfoDTO docInfoNew = JSON.parseObject(snapshotNew.getContent(), DocInfoDTO.class);
            DocDiffRecord updateRecord = this.createRecord(docInfoOld, docDiffDTO, ModifyType.UPDATE);
            docDiffDetailService.doCompare(docInfoOld, docInfoNew, updateRecord);
        }
    }

    private DocDiffRecord createRecord(DocInfoDTO docInfoDTO, DocDiffDTO docDiffDTO, ModifyType modifyType) {
        User user = docDiffDTO.getUser();
        DocDiffRecord docDiffRecord = new DocDiffRecord();
        docDiffRecord.setDocId(docInfoDTO.getId());
        docDiffRecord.setMd5Old(docDiffDTO.getMd5Old());
        docDiffRecord.setMd5New(docDiffDTO.getMd5New());
        docDiffRecord.setModifySource(docDiffDTO.getModifySource().getSource());
        docDiffRecord.setModifyUserId(user.getUserId());
        docDiffRecord.setModifyNickname(user.getNickname());
        docDiffRecord.setModifyType(modifyType.getType());
        this.save(docDiffRecord);
        return docDiffRecord;
    }

}