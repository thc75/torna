package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.User;
import cn.torna.common.enums.OperationMode;
import cn.torna.common.enums.ParamStyleEnum;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.DataIdUtil;
import cn.torna.common.util.IdGen;
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

    /**
     * 删除参数，同时会删除子节点
     * @param id id
     */
    public void deleteParamDeeply(long id) {
        this.getMapper().deleteById(id);
        this.deleteChildrenDeeply(id);
    }

    /**
     * 递归删除下面所有子节点
     * @param parentId 父id
     */
    private void deleteChildrenDeeply(long parentId) {
        List<DocParam> children = this.list("parent_id", parentId);
        for (DocParam child : children) {
            this.deleteParamDeeply(child.getId());
        }
    }

    private void doSave(DocParamDTO docParamDTO, long parentId, DocInfo docInfo, ParamStyleEnum paramStyleEnum, User user) {
        DocParam docParam = new DocParam();
        Long docId = docInfo.getId();
        String dataId = DataIdUtil.getDocParamDataId(docId, parentId, paramStyleEnum.getStyle(), docParamDTO.getName());
        // 如果删除
        if (Booleans.isTrue(docParamDTO.getIsDeleted())) {
            dataId = IdGen.nextId();
        }
        docParam.setId(docParamDTO.getId());
        docParam.setDataId(dataId);
        docParam.setName(docParamDTO.getName());
        docParam.setType(docParamDTO.getType());
        docParam.setRequired(docParamDTO.getRequired());
        docParam.setMaxLength(buildMaxLength(docParamDTO));
        docParam.setExample(docParamDTO.getExample());
        docParam.setDescription(docParamDTO.getDescription());
        docParam.setEnumId(buildEnumId(docInfo.getModuleId(), docParamDTO));
        docParam.setDocId(docId);
        docParam.setParentId(parentId);
        docParam.setStyle(paramStyleEnum.getStyle());
        docParam.setCreatorId(user.getUserId());
        docParam.setCreateMode(user.getOperationModel());
        docParam.setCreatorName(user.getNickname());
        docParam.setModifierId(user.getUserId());
        docParam.setModifyMode(user.getOperationModel());
        docParam.setModifierName(user.getNickname());
        docParam.setOrderIndex(docParamDTO.getOrderIndex());
        docParam.setIsDeleted(docParamDTO.getIsDeleted());
        if (docParam.getDescription() == null) {
            docParam.setDescription("");
        }
        DocParam savedParam;
        if (docParam.getId() == null) {
            savedParam = this.saveParam(docParam);
        } else {
            this.update(docParam);
            savedParam = docParam;
        }
        List<DocParamDTO> children = docParamDTO.getChildren();
        if (children != null) {
            Long pid = savedParam.getId();
            // 修复NPE问题
            if (pid == null) {
                DocParam exist = getByDataId(savedParam.getDataId());
                if (exist != null) {
                    pid = exist.getId();
                }
            }
            for (DocParamDTO child : children) {
                // 如果父节点被删除，子节点也要删除
                if (docParam.getIsDeleted() == Booleans.TRUE) {
                    child.setIsDeleted(docParam.getIsDeleted());
                }
                if (pid == null) {
                    continue;
                }
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
            // 如果枚举名称为空则使用字段描述
            if (StringUtils.isEmpty(enumInfoDTO.getName())) {
                String paramDescription = docParamDTO.getDescription();
                String name = StringUtils.isEmpty(paramDescription) ? docParamDTO.getName() : paramDescription;
                enumInfoDTO.setName(name);
            }
            enumInfoDTO.setModuleId(moduleId);
            EnumInfo enumInfo = enumService.saveEnumInfo(enumInfoDTO);
            return enumInfo.getId();
        }
        Long enumId = docParamDTO.getEnumId();
        if (enumId == null) {
            enumId = 0L;
        }
        return enumId;
    }

    public DocParam saveParam(DocParam docParam) {
        if (docParam.getDescription() == null) {
            docParam.setDescription("");
        }
        if (docParam.getExample() == null) {
            docParam.setExample("");
        }
        this.getMapper().saveParam(docParam);
        return docParam;
    }

    public void deletePushParam(List<Long> docIdList) {
        // 删除文档对应的参数
        Query paramDelQuery = new Query()
                .in("doc_id", docIdList)
                .eq("create_mode", OperationMode.OPEN.getType())
                ;
        getMapper().deleteByQuery(paramDelQuery);
    }


}
