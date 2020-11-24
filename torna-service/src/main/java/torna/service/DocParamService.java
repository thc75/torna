package torna.service;

import com.gitee.fastmybatis.core.query.Query;
import org.springframework.stereotype.Service;
import torna.common.bean.Booleans;
import torna.common.bean.User;
import torna.common.enums.OperationMode;
import torna.common.enums.ParamStyleEnum;
import torna.common.support.BaseService;
import torna.dao.entity.DocInfo;
import torna.dao.entity.DocParam;
import torna.dao.mapper.DocParamMapper;
import torna.service.dto.DocParamDTO;

import java.util.List;

/**
 * @author tanghc
 */
@Service
public class DocParamService extends BaseService<DocParam, DocParamMapper> {

    public DocParam getByDocIdAndParentIdAndName(long docId, long parentId, String name) {
        Query query = new Query()
                .eq("doc_id", docId)
                .eq("parent_id", parentId)
                .eq("name", name);
        return get(query);
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
        docParam.setId(docParamDTO.getId());
        docParam.setName(docParamDTO.getName());
        docParam.setType(docParamDTO.getType());
        docParam.setRequired(docParamDTO.getRequired());
        docParam.setMaxLength(docParamDTO.getMaxLength());
        docParam.setExample(docParamDTO.getExample());
        docParam.setDescription(docParamDTO.getDescription());
        docParam.setEnumContent(docParamDTO.getEnumContent());
        docParam.setDocId(docInfo.getId());
        docParam.setParentId(parentId);
        docParam.setStyle(paramStyleEnum.getStyle());
        docParam.setModifyMode(user.getOperationModel());
        docParam.setModifierId(user.getUserId());
        docParam.setIsDeleted(docParamDTO.getIsDeleted());
        DocParam savedParam = this.saveParam(docParam);
        List<DocParamDTO> children = docParamDTO.getChildren();
        if (children != null) {
            Long id = savedParam.getId();
            for (DocParamDTO child : children) {
                this.doSave(child, id, docInfo, paramStyleEnum, user);
            }
        }
    }

    public DocParam saveParam(DocParam docParam) {
        Long id = docParam.getId();
        DocParam docParamExist = id != null && id > 0 ? this.getById(id) : null;
        if (docParamExist != null) {
            if (docParam.getIsDeleted() != null && docParam.getIsDeleted() == Booleans.TRUE) {
                this.delete(docParamExist);
                return docParamExist;
            }
            docParamExist.setName(docParam.getName());
            docParamExist.setType(docParam.getType());
            docParamExist.setRequired(docParam.getRequired());
            docParamExist.setMaxLength(docParam.getMaxLength());
            docParamExist.setExample(docParam.getExample());
            docParamExist.setDescription(docParam.getDescription());
            docParamExist.setEnumContent(docParam.getEnumContent());
            docParamExist.setStyle(docParam.getStyle());
            docParamExist.setModifyMode(docParam.getModifyMode());
            docParamExist.setModifierId(docParam.getModifierId());
            docParamExist.setIsDeleted(Booleans.FALSE);
            updateIgnoreNull(docParamExist);
            return docParamExist;
        } else {
            saveIgnoreNull(docParam);
            return docParam;
        }
    }

}
