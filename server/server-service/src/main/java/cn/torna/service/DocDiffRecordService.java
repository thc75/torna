package cn.torna.service;

import cn.torna.common.bean.Configs;
import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.bean.User;
import cn.torna.common.enums.ModifyType;
import cn.torna.common.enums.SourceFromEnum;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.DingTalkUtil;
import cn.torna.common.util.IdUtil;
import cn.torna.common.util.ThreadPoolUtil;
import cn.torna.dao.entity.DocDiffDetail;
import cn.torna.dao.entity.DocDiffRecord;
import cn.torna.dao.entity.DocSnapshot;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.Project;
import cn.torna.dao.mapper.DocDiffRecordMapper;
import cn.torna.dao.mapper.ModuleMapper;
import cn.torna.dao.mapper.ProjectMapper;
import cn.torna.service.dto.DocDiffDTO;
import cn.torna.service.dto.DocDiffDetailDTO;
import cn.torna.service.dto.DocDiffDetailWrapperDTO;
import cn.torna.service.dto.DocDiffRecordDTO;
import cn.torna.service.dto.DocInfoDTO;
import com.alibaba.fastjson.JSON;
import com.gitee.fastmybatis.core.query.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

    private static final DateTimeFormatter YMDHMS_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private DocSnapshotService docSnapshotService;

    @Autowired
    private DocDiffDetailService docDiffDetailService;

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private ModuleConfigService moduleConfigService;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private ModuleMapper moduleMapper;

    @Autowired
    private DocInfoProService docInfoProService;

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


    public void doDocDiff(String oldMd5, DocInfoDTO docInfoDTO, SourceFromEnum sourceEnum, User user) {
        doDocDiffNow(oldMd5, docInfoDTO, sourceEnum, user, DocDiffContext::addQueue);
    }

    public boolean existRecord(String oldMd5, String newMd5) {
        if (oldMd5 == null) {
            return false;
        }
        Query query = new Query()
                .eq("md5_old", oldMd5)
                .eq("md5_new", newMd5);
        return this.get(query) != null;
    }

    public void doDocDiffNow(String oldMd5, DocInfoDTO docInfoDTO, SourceFromEnum sourceEnum, User user, Consumer<DocDiffDTO> consumer) {
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
        } else if (snapshotOld != null && snapshotNew != null) {
            // update
            DocInfoDTO docInfoOld = JSON.parseObject(snapshotOld.getContent(), DocInfoDTO.class);
            DocInfoDTO docInfoNew = JSON.parseObject(snapshotNew.getContent(), DocInfoDTO.class);
            modifyType = ModifyType.UPDATE;
            DocDiffRecord updateRecord = this.createRecord(docInfoOld, docDiffDTO, modifyType);
            docDiffDetailService.doCompare(docInfoOld, docInfoNew, updateRecord);
            this.pushMessage(docInfoNew, modifyType);
        }
    }

    /**
     * 推送钉钉消息
     * @param docInfoDTO
     * @param modifyType
     */
    private void pushMessage(DocInfoDTO docInfoDTO, ModifyType modifyType) {
        try {
            if (modifyType == ModifyType.UPDATE || modifyType == ModifyType.DELETE) {
                String url = moduleConfigService.getDingDingRobotWebhookUrl(docInfoDTO.getModuleId());
                if (StringUtils.isEmpty(url)) {
                    return;
                }
                List<String> dingDingUserIds = docInfoProService.listSubscribeDocDingDingUserIds(docInfoDTO.getId());
                String content = buildDingDingMessage(docInfoDTO, modifyType, dingDingUserIds);
                if (StringUtils.isEmpty(content)) {
                    return;
                }
                DingTalkUtil.pushRobotMessage(url, content, dingDingUserIds);
            }
        } catch (Exception e) {
            log.error("推送钉钉消息失败, doc={}", docInfoDTO.getName(), e);
        }
    }

    private String buildDingDingMessage(DocInfoDTO docInfoDTO, ModifyType modifyType, List<String> userIds) {
        Project project = projectMapper.getById(docInfoDTO.getProjectId());
        Module module = moduleMapper.getById(docInfoDTO.getModuleId());
        Map<String, String> replaceMap = new HashMap<>(16);
        replaceMap.put("{projectName}", project.getName());
        replaceMap.put("{appName}", module.getName());
        replaceMap.put("{docName}", docInfoDTO.getName());
        replaceMap.put("{url}", docInfoDTO.getUrl());
        replaceMap.put("{modifier}", docInfoDTO.getModifierName());
        replaceMap.put("{modifyTime}", docInfoDTO.getGmtModified().format(YMDHMS_PATTERN));
        replaceMap.put("{modifyType}", modifyType.getDescription());
        String frontUrl = EnvironmentKeys.TORNA_FRONT_URL.getValue("http://localhost:7700");
        String docViewUrl = frontUrl + "/#/view/" + IdUtil.encode(docInfoDTO.getId());
        replaceMap.put("{docViewUrl}", docViewUrl);
        String atUser = "";
        if (!CollectionUtils.isEmpty(userIds)) {
            atUser = userIds.stream()
                    .map(userId -> "@" + userId)
                    .collect(Collectors.joining(" "));
        }
        replaceMap.put("{@user}", atUser);

        String content = EnvironmentKeys.PUSH_DINGDING_WEBHOOK_CONTENT.getValue();
        if (StringUtils.isEmpty(content)) {
            return content;
        }
        for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue());
        }
        return content;
    }

    private DocDiffRecord createRecord(DocInfoDTO docInfoDTO, DocDiffDTO docDiffDTO, ModifyType modifyType) {
        User user = docDiffDTO.getUser();
        DocDiffRecord docDiffRecord = new DocDiffRecord();
        docDiffRecord.setDocId(docInfoDTO.getId());
        docDiffRecord.setMd5Old(docDiffDTO.getMd5Old());
        docDiffRecord.setMd5New(docDiffDTO.getMd5New());
        docDiffRecord.setModifySource(docDiffDTO.getSourceFromEnum().getSource());
        docDiffRecord.setModifyUserId(user.getUserId());
        docDiffRecord.setModifyNickname(user.getNickname());
        docDiffRecord.setModifyType(modifyType.getType());
        this.save(docDiffRecord);
        return docDiffRecord;
    }

    /**
     * 还原
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

}