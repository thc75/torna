package cn.torna.service;

import cn.torna.common.bean.User;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.Module;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocRefDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author thc
 */
@Service
public class DocInfoProService {

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private ModuleService moduleService;

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
