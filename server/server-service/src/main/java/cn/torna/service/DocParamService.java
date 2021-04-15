package cn.torna.service;

import cn.torna.common.bean.User;
import cn.torna.common.enums.ParamStyleEnum;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.DataIdUtil;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.DocParam;
import cn.torna.dao.entity.EnumInfo;
import cn.torna.dao.mapper.DocParamMapper;
import cn.torna.service.dto.DocParamDTO;
import cn.torna.service.dto.EnumInfoDTO;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        // 如果参数是空的，则移除这个类型的所有参数
        if (CollectionUtils.isEmpty(docParamDTOS)) {
            return;
        }
        for (DocParamDTO docParamDTO : docParamDTOS) {
            this.doSave(docParamDTO, 0L, docInfo, paramStyleEnum, user);
        }
    }

    private List<DocParam> listParentParam(long docId, ParamStyleEnum paramStyleEnum) {
        Query query = new Query()
                .eq("doc_id", docId)
                .eq("style", paramStyleEnum.getStyle())
                .eq("parent_id", 0);
        return this.list(query);
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
        docParam.setCreatorId(user.getUserId());
        docParam.setCreateMode(user.getOperationModel());
        docParam.setCreatorName(user.getNickname());
        docParam.setModifierId(user.getUserId());
        docParam.setModifyMode(user.getOperationModel());
        docParam.setModifierName(user.getNickname());
        docParam.setIsDeleted(docParamDTO.getIsDeleted());
        DocParam savedParam = this.saveParam(docParam);
        List<DocParamDTO> children = docParamDTO.getChildren();
        if (children != null) {
            Long pid = savedParam.getId();
            for (DocParamDTO child : children) {
                this.doSave(child, pid, docInfo, paramStyleEnum, user);
            }
        }
    }

    private static String buildMaxLength(DocParamDTO docParamDTO) {
        String maxLength = docParamDTO.getMaxLength();
        if (StringUtils.isEmpty(maxLength) || "0".equals(maxLength)) {
            maxLength = "-";
        }
        return CollectionUtils.isEmpty(docParamDTO.getChildren()) ? maxLength : "";
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

    public DocParam saveParam(DocParam docParam) {
        this.getMapper().saveParam(docParam);
        return docParam;
    }


}
