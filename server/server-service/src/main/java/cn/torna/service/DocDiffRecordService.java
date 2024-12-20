package cn.torna.service;

import cn.torna.common.bean.User;
import cn.torna.common.enums.ModifySourceEnum;
import cn.torna.common.enums.ModifyType;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.DocDiffDetail;
import cn.torna.dao.entity.DocDiffRecord;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.DocSnapshot;
import cn.torna.dao.mapper.DocDiffRecordMapper;
import cn.torna.service.dto.DocDiffDTO;
import cn.torna.service.dto.DocDiffDetailDTO;
import cn.torna.service.dto.DocDiffDetailWrapperDTO;
import cn.torna.service.dto.DocDiffRecordDTO;
import cn.torna.service.dto.DocInfoDTO;
import com.alibaba.fastjson.JSON;
import com.gitee.fastmybatis.core.query.LambdaQuery;
import com.gitee.fastmybatis.core.query.LambdaQuery;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
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
public class DocDiffRecordService extends BaseLambdaService<DocDiffRecord, DocDiffRecordMapper> {

    @Autowired
    private DocSnapshotService docSnapshotService;

    @Autowired
    private DocDiffDetailService docDiffDetailService;

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private MessageService messageService;


    /**
     * 获取修改记录
     *
     * @param docId docId
     * @return
     */
    public List<DocDiffRecordDTO> listDocDiff(Long docId) {
        DocInfo docInfo = docInfoService.getById(docId);
        String version = docInfo.getVersion();
        List<DocDiffRecord> diffRecordList;
        // 如果有版本号，只能看截止到当前版本的记录
        if (StringUtils.hasText(version) && !"-".equals(version)) {
            diffRecordList = list(DocDiffRecord::getDocId, docId);
        } else {
            // 可以查看所有变更记录
            String docKey = docInfoService.getDocKey(docId);
            diffRecordList = list(DocDiffRecord::getDocKey, docKey);
        }
        if (CollectionUtils.isEmpty(diffRecordList)) {
            return Collections.emptyList();
        }
        List<Long> ids = diffRecordList.stream().map(DocDiffRecord::getId).collect(Collectors.toList());
        List<DocDiffDetail> docDiffDetails = docDiffDetailService.list(DocDiffDetail::getRecordId, ids);
        // KEY:recordId
        Map<Long, List<DocDiffDetail>> recordDetailMap = docDiffDetails.stream()
                .collect(Collectors.groupingBy(DocDiffDetail::getRecordId));
        diffRecordList.sort(Comparator.comparing(DocDiffRecord::getGmtCreate, Comparator.reverseOrder()));

        return diffRecordList.stream()
                .map(docDiffRecord -> {
                    DocDiffRecordDTO docDiffRecordDTO = CopyUtil.copyBean(docDiffRecord, DocDiffRecordDTO::new);
                    List<DocDiffDetail> docDiffDetailList = recordDetailMap.get(docDiffRecord.getId());
                    List<DocDiffDetailDTO> details = buildDocDiffDetails(docDiffDetailList);
                    List<DocDiffDetailWrapperDTO> wrapper = buildWrappers(details);
                    docDiffRecordDTO.setDocDiffWrappers(wrapper);
                    return docDiffRecordDTO;
                })
                .collect(Collectors.toList());
    }

    // 根据positionType分类
    private List<DocDiffDetailWrapperDTO> buildWrappers(List<DocDiffDetailDTO> details) {
        return details.stream()
                .collect(Collectors.groupingBy(DocDiffDetailDTO::getPositionType))
                .entrySet()
                .stream()
                .map(entry -> new DocDiffDetailWrapperDTO(entry.getKey(), entry.getValue()))
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


    public void doDocDiff(String oldMd5, DocInfoDTO docInfoDTO, ModifySourceEnum sourceEnum, User user) {
        doDocDiffNow(oldMd5, docInfoDTO, sourceEnum, user, DocDiffContext::addQueue);
    }

    public boolean existRecord(String oldMd5, String newMd5) {
        if (oldMd5 == null) {
            return false;
        }
        Query query = this.query()
                .eq(DocDiffRecord::getMd5Old, oldMd5)
                .eq(DocDiffRecord::getMd5New, newMd5);
        return this.get(query) != null;
    }

    public void doDocDiffNow(String oldMd5, DocInfoDTO docInfoDTO, ModifySourceEnum sourceEnum, User user, Consumer<DocDiffDTO> consumer) {
        String newMd5 = docInfoDTO.getMd5();
        boolean contentChanged = !Objects.equals(oldMd5, newMd5);
        // 文档内容被修改，做相关处理
        if (contentChanged) {
            // 保存新md5内容
            docSnapshotService.saveDocSnapshot(docInfoDTO);
            consumer.accept(new DocDiffDTO(oldMd5, newMd5, LocalDateTime.now(), user, sourceEnum));
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

        ModifyType modifyType;
        // del
        if (snapshotOld != null && snapshotNew == null) {
            DocInfoDTO docInfoOld = JSON.parseObject(snapshotOld.getContent(), DocInfoDTO.class);
            modifyType = ModifyType.DELETE;
            this.createRecord(docInfoOld, docDiffDTO, modifyType);
            this.pushMessage(docInfoOld, modifyType);
        } else if (snapshotOld == null && snapshotNew != null) {
            // new
            DocInfoDTO docInfoNew = JSON.parseObject(snapshotNew.getContent(), DocInfoDTO.class);
            modifyType = ModifyType.ADD;
            this.createRecord(docInfoNew, docDiffDTO, modifyType);
            this.pushMessage(docInfoNew, modifyType);
        } else if (snapshotOld != null && snapshotNew != null) {
            // update
            DocInfoDTO docInfoOld = JSON.parseObject(snapshotOld.getContent(), DocInfoDTO.class);
            DocInfoDTO docInfoNew = JSON.parseObject(snapshotNew.getContent(), DocInfoDTO.class);
            modifyType = ModifyType.UPDATE;
            DocDiffRecord updateRecord = this.createRecord(docInfoOld, docDiffDTO, modifyType);
            if (!Objects.equals(docDiffDTO.getModifySourceEnum(), ModifySourceEnum.TEXT)) {
                docDiffDetailService.doCompare(docInfoOld, docInfoNew, updateRecord);
            }
            this.pushMessage(docInfoNew, modifyType);
        }
    }

    /**
     * 推送钉钉/企业微信 消息
     *
     * @param docInfoDTO
     * @param modifyType
     */
    private void pushMessage(DocInfoDTO docInfoDTO, ModifyType modifyType) {
        messageService.pushDocModifyMessage(docInfoDTO, modifyType);
    }

    private DocDiffRecord createRecord(DocInfoDTO docInfoDTO, DocDiffDTO docDiffDTO, ModifyType modifyType) {
        User user = docDiffDTO.getUser();
        DocDiffRecord docDiffRecord = new DocDiffRecord();
        docDiffRecord.setDocId(docInfoDTO.getId());
        docDiffRecord.setDocKey(docInfoDTO.buildDocKey());
        docDiffRecord.setMd5Old(docDiffDTO.getMd5Old());
        docDiffRecord.setMd5New(docDiffDTO.getMd5New());
        docDiffRecord.setModifySource(docDiffDTO.getModifySourceEnum().getSource());
        docDiffRecord.setModifyUserId(user.getUserId());
        docDiffRecord.setModifyNickname(user.getNickname());
        docDiffRecord.setModifyType(modifyType.getType());
        docDiffRecord.setModifyTime(docDiffDTO.getModifyTime());
        this.save(docDiffRecord);
        return docDiffRecord;
    }

    /**
     * 还原
     *
     * @param id
     */
    public void restore(Long id, User user) {
        Objects.requireNonNull(id);
        DocDiffRecord docDiffRecord = getById(id);
        String md5New = docDiffRecord.getMd5New();
        DocSnapshot docSnapshot = docSnapshotService.getByMd5(md5New);
        String content = docSnapshot.getContent();
        DocInfoDTO docInfoDTO = JSON.parseObject(content, DocInfoDTO.class);
        docInfoService.doUpdateDocInfo(docInfoDTO, user);
    }


    public void fillDocKey() {
        List<DocDiffRecord> list = this.list(new Query());
        for (DocDiffRecord docDiffRecord : list) {
            Long docId = docDiffRecord.getDocId();
            String docKey = docInfoService.getDocKey(docId);
            if (StringUtils.hasText(docKey)) {
                this.query()
                        .set(DocDiffRecord::getDocKey, docKey)
                        .eq(DocDiffRecord::getId, docDiffRecord.getId())
                        .update();
            }
        }
    }

}
