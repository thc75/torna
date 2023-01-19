package cn.torna.service;

import cn.torna.common.bean.User;
import cn.torna.common.enums.UserSubscribeTypeEnum;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.UserDingtalkInfo;
import cn.torna.dao.mapper.UserDingtalkInfoMapper;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocRefDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author thc
 */
@Service
public class DocInfoProService {

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private UserSubscribeService userSubscribeService;

    @Resource
    private UserDingtalkInfoMapper userDingtalkInfoMapper;

    /**
     * 获取文档所有的关注人员的钉钉userid
     * @param docId
     * @return 返回钉钉的userid
     */
    public List<String> listSubscribeDocDingDingUserIds(long docId) {
        List<Long> userIds = userSubscribeService.listUserIds(UserSubscribeTypeEnum.DOC, docId);
        List<UserDingtalkInfo> dingtalkInfoList = userDingtalkInfoMapper.listByCollection("user_info_id", userIds);
        return dingtalkInfoList.stream()
                .map(UserDingtalkInfo::getUserid)
                .collect(Collectors.toList());
    }

    public DocRefDTO getDocRefInfo(long docId) {
        DocRefDTO docRefDTO = new DocRefDTO();
        DocInfo docInfo = docInfoService.getById(docId);
        docRefDTO.setDocId(docId);
        docRefDTO.setModuleId(docInfo.getModuleId());
        Module module = moduleService.getById(docInfo.getModuleId());
        docRefDTO.setProjectId(module.getProjectId());
        return docRefDTO;
    }

    /**
     * 更新文档状态
     * @param docId
     * @param status
     * @param user
     */
    public void updateStatus(long docId, byte status, User user) {
        DocInfoDTO docDetail = docInfoService.getDocForm(docId);
        if (Objects.equals(status, docDetail.getStatus())) {
            return;
        }
        docDetail.setStatus(status);
        docInfoService.doUpdateDocBaseInfo(docDetail, user);
    }

}
