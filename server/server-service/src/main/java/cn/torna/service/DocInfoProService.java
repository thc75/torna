package cn.torna.service;

import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.Module;
import cn.torna.service.dto.DocRefDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
