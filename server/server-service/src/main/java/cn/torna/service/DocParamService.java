package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.User;
import cn.torna.common.enums.ParamStyleEnum;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.DataIdUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.DocParam;
import cn.torna.dao.entity.EnumInfo;
import cn.torna.dao.mapper.DocParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.torna.service.dto.DocParamDTO;
import cn.torna.service.dto.EnumInfoDTO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author tanghc
 */
@Service
public class DocParamService extends BaseService<DocParam, DocParamMapper> {

    @Autowired
    private EnumService enumService;

    public DocParam getByDataId(String dataId) {
        return get("data_id", dataId);
    }

    public void saveParams(DocInfo docInfo, List<DocParamDTO> docParamDTOS, ParamStyleEnum paramStyleEnum, User user) {
        if (docParamDTOS == null) {
            return;
        }
        for (DocParamDTO docParamDTO : docParamDTOS) {
            this.doSave(docParamDTO, 0L, docInfo, paramStyleEnum, user);
        }
    }

    private void doSave(DocParamDTO docParamDTO, long parentId, DocInfo docInfo, ParamStyleEnum paramStyleEnum, User user) {
        DocParam docParam = new DocParam();
        String dataId = DataIdUtil.getDocParamDataId(docInfo.getId(), parentId, paramStyleEnum.getStyle(), docParamDTO.getName());
        docParam.setId(docParamDTO.getId());
        docParam.setDataId(dataId);
        docParam.setName(docParamDTO.getName());
        docParam.setType(docParamDTO.getType());
        docParam.setRequired(docParamDTO.getRequired());
        docParam.setMaxLength(buildMaxLength(docParamDTO));
        docParam.setExample(docParamDTO.getExample());
        docParam.setDescription(docParamDTO.getDescription());
        docParam.setEnumId(buildEnumId(docInfo.getModuleId(), docParamDTO));
        docParam.setDocId(docInfo.getId());
        docParam.setParentId(parentId);
        docParam.setStyle(paramStyleEnum.getStyle());
        docParam.setModifyMode(user.getOperationModel());
        docParam.setModifierId(user.getUserId());
        docParam.setModifierName(user.getNickname());
        docParam.setIsDeleted(docParamDTO.getIsDeleted());
        DocParam savedParam = this.saveParam(docParam, user);
        List<DocParamDTO> children = docParamDTO.getChildren();
        if (children != null) {
            Long pid = savedParam.getId();
            for (DocParamDTO child : children) {
                this.doSave(child, pid, docInfo, paramStyleEnum, user);
            }
        }
    }

    private static String buildMaxLength(DocParamDTO docParamDTO) {
        return CollectionUtils.isEmpty(docParamDTO.getChildren()) ? docParamDTO.getMaxLength() : "";
    }

    private Long buildEnumId(long moduleId, DocParamDTO docParamDTO) {
        EnumInfoDTO enumInfoDTO = docParamDTO.getEnumInfo();
        if (enumInfoDTO != null) {
            enumInfoDTO.setModuleId(moduleId);
            EnumInfo enumInfo = enumService.saveEnumInfo(enumInfoDTO);
            return enumInfo.getId();
        }
        return docParamDTO.getEnumId();
    }

    public DocParam saveParam(DocParam docParam, User user) {
        DocParam docParamExist;
        Long id = docParam.getId();
        String dataId = docParam.getDataId();
        if (id != null) {
            docParamExist = getById(id);
        } else {
            docParamExist = getByDataId(dataId);
        }
        if (docParamExist != null) {
            if (docParam.getIsDeleted() != null && docParam.getIsDeleted() == Booleans.TRUE) {
                this.delete(docParamExist);
                return docParamExist;
            }
            docParamExist.setDataId(docParam.getDataId());
            docParamExist.setName(docParam.getName());
            docParamExist.setType(docParam.getType());
            docParamExist.setRequired(docParam.getRequired());
            docParamExist.setMaxLength(docParam.getMaxLength());
            docParamExist.setExample(docParam.getExample());
            docParamExist.setDescription(docParam.getDescription());
            docParamExist.setEnumId(docParam.getEnumId());
            docParamExist.setDocId(docParam.getDocId());
            docParamExist.setParentId(docParam.getParentId());
            docParamExist.setStyle(docParam.getStyle());
            docParamExist.setModifyMode(docParam.getModifyMode());
            docParamExist.setModifierId(docParam.getModifierId());
            docParamExist.setModifierName(docParam.getModifierName());
            docParamExist.setIsDeleted(Booleans.FALSE);
            update(docParamExist);
            return docParamExist;
        } else {
            docParam.setCreatorId(user.getUserId());
            docParam.setCreateMode(user.getOperationModel());
            docParam.setCreatorName(user.getNickname());
            docParam.setModifierId(user.getUserId());
            docParam.setModifyMode(user.getOperationModel());
            docParam.setModifierName(user.getNickname());
            save(docParam);
            return docParam;
        }
    }

}
