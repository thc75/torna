package cn.torna.service;

import cn.torna.common.bean.User;
import cn.torna.common.enums.DocDiffModifySourceEnum;
import cn.torna.common.enums.ModifyType;
import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.DocDiffRecord;
import cn.torna.dao.entity.DocSnapshot;
import cn.torna.dao.mapper.DocDiffRecordMapper;
import cn.torna.service.dto.DocDiffDTO;
import cn.torna.service.dto.DocDiffRecordDTO;
import cn.torna.service.dto.DocInfoDTO;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * @author tanghc
 */
@Service
public class DocDiffRecordService extends BaseService<DocDiffRecord, DocDiffRecordMapper> {

    @Autowired
    private DocSnapshotService docSnapshotService;

    @Autowired
    private DocDiffDetailService docDiffDetailService;

    // TODO：获取修改记录
    /**
     * 获取修改记录
     * @param dataId
     * @return
     */
    public List<DocDiffRecordDTO> listDocDiff(String dataId) {
        return null;
    }


    public void doDocDiff(String oldMd5, DocInfoDTO docInfoDTO, DocDiffModifySourceEnum sourceEnum, User user) {
        doDocDiffNow(oldMd5, docInfoDTO, sourceEnum, user, DocDiffContext::addQueue);
    }

    public void doDocDiffNow(String oldMd5, DocInfoDTO docInfoDTO, DocDiffModifySourceEnum sourceEnum, User user, Consumer<DocDiffDTO> consumer) {
        String newMd5 = docInfoDTO.getMd5();
        boolean contentChanged = Objects.equals(oldMd5, newMd5);
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
        DocSnapshot snapshotOld = docSnapshotService.getByMd5(md5Old);
        DocSnapshot snapshotNew = docSnapshotService.getByMd5(md5New);

        // del
        if (snapshotOld != null && snapshotNew == null) {
            DocInfoDTO docInfoOld = JSON.parseObject(snapshotOld.getContent(), DocInfoDTO.class);
            this.createDelRecord(docInfoOld, docDiffDTO);
        } else if (snapshotOld == null && snapshotNew != null) {
            // new
            DocInfoDTO docInfoNew = JSON.parseObject(snapshotNew.getContent(), DocInfoDTO.class);
            this.createNewRecord(docInfoNew, docDiffDTO);
        } else if (snapshotOld != null && snapshotNew != null) {
            // update
            DocInfoDTO docInfoOld = JSON.parseObject(snapshotOld.getContent(), DocInfoDTO.class);
            DocInfoDTO docInfoNew = JSON.parseObject(snapshotNew.getContent(), DocInfoDTO.class);
            DocDiffRecord updateRecord = this.createUpdateRecord(docInfoOld, docInfoNew, docDiffDTO);
            docDiffDetailService.doCompare(docInfoOld, docInfoNew, updateRecord, docDiffDTO.getUser().getNickname());
        }
    }

    private void createNewRecord(DocInfoDTO docInfoNew, DocDiffDTO docDiffDTO) {
        User user = docDiffDTO.getUser();
        DocDiffRecord docDiffRecord = new DocDiffRecord();
        docDiffRecord.setDataId(docInfoNew.buildDataId());
        docDiffRecord.setMd5Old("");
        docDiffRecord.setMd5New(docInfoNew.getMd5());
        docDiffRecord.setModifySource(docDiffDTO.getModifySource().getSource());
        docDiffRecord.setModifyUserId(user.getUserId());
        docDiffRecord.setModifyNickname(user.getNickname());
        docDiffRecord.setModifyType(ModifyType.ADD.getType());
        this.save(docDiffRecord);
    }

    private void createDelRecord(DocInfoDTO docInfoOld, DocDiffDTO docDiffDTO) {
        User user = docDiffDTO.getUser();
        DocDiffRecord docDiffRecord = new DocDiffRecord();
        docDiffRecord.setDataId(docInfoOld.buildDataId());
        docDiffRecord.setMd5Old(docInfoOld.getMd5());
        docDiffRecord.setMd5New("");
        docDiffRecord.setModifySource(docDiffDTO.getModifySource().getSource());
        docDiffRecord.setModifyUserId(user.getUserId());
        docDiffRecord.setModifyNickname(user.getNickname());
        docDiffRecord.setModifyType(ModifyType.DELETE.getType());
        this.save(docDiffRecord);
    }

    private DocDiffRecord createUpdateRecord(DocInfoDTO docInfoOld, DocInfoDTO docInfoNew, DocDiffDTO docDiffDTO) {
        User user = docDiffDTO.getUser();
        DocDiffRecord docDiffRecord = new DocDiffRecord();
        docDiffRecord.setDataId(docInfoOld.buildDataId());
        docDiffRecord.setMd5Old(docInfoOld.getMd5());
        docDiffRecord.setMd5New(docInfoNew.getMd5());
        docDiffRecord.setModifySource(docDiffDTO.getModifySource().getSource());
        docDiffRecord.setModifyUserId(user.getUserId());
        docDiffRecord.setModifyNickname(user.getNickname());
        docDiffRecord.setModifyType(ModifyType.UPDATE.getType());
        this.save(docDiffRecord);
        return docDiffRecord;
    }

}