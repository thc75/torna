package cn.torna.service;

import cn.torna.common.annotation.Diff;
import cn.torna.common.enums.ModifyType;
import cn.torna.common.enums.PositionType;
import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.DocDiffDetail;
import cn.torna.dao.entity.DocDiffRecord;
import cn.torna.dao.mapper.DocDiffDetailMapper;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocParamDTO;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author tanghc
 */
@Service
public class DocDiffDetailService extends BaseService<DocDiffDetail, DocDiffDetailMapper> {

    /**
     * 比较两个文档对象不同的地方
     *
     * @param docInfoOld 老文档
     * @param docInfoNew 新文档
     */
    public void doCompare(DocInfoDTO docInfoOld, DocInfoDTO docInfoNew, DocDiffRecord docDiffRecord) {
        List<DocDiffDetail> docDiffDetails = new ArrayList<>();

        ReflectionUtils.doWithFields(docInfoOld.getClass(), field -> {
            List<DiffBean> diffBeans = buildDocDiffBeans(field, docInfoOld, docInfoNew);
            for (DiffBean diffBean : diffBeans) {
                if (diffBean.getModifyType() != ModifyType.NONE) {
                    DocDiffDetail docDiffDetail = buildDocDiffDetail(docDiffRecord, diffBean);
                    docDiffDetails.add(docDiffDetail);
                }
            }
        }, field -> AnnotationUtils.getAnnotation(field, Diff.class) != null);

        this.saveBatch(docDiffDetails);
    }

    private DocDiffDetail buildDocDiffDetail(DocDiffRecord docDiffRecord, DiffBean diffBean) {
        DocDiffDetail docDiffDetail = new DocDiffDetail();
        docDiffDetail.setTargetName(diffBean.getTargetName());
        docDiffDetail.setPositionType(diffBean.getPositionType().getType());
        docDiffDetail.setRecordId(docDiffRecord.getId());
        Map<String, Object> content = buildValue(diffBean);
        docDiffDetail.setContent(JSON.toJSONString(content));
        docDiffDetail.setModifyType(diffBean.getModifyType().getType());
        return docDiffDetail;
    }

    private Map<String, Object> buildValue(DiffBean diffBean) {
        /*
        {
            value: { old: '', new: '' }
            targetName: '',
            modifyType: 1
        }
         */
        Map<String, Object> value = new LinkedHashMap<>(8);
        OldNewValue oldNewValue = new OldNewValue(diffBean.getOldValue(), diffBean.getNewValue());
        value.put("value", oldNewValue);
        value.put("targetName", diffBean.getTargetName());
        return value;
    }

    private static List<DiffBean> buildDocDiffBeans(Field field, DocInfoDTO docInfoOld, DocInfoDTO docInfoNew) {
        field.setAccessible(true);
        String name = field.getName();
        Diff diff = AnnotationUtils.getAnnotation(field, Diff.class);
        Objects.requireNonNull(diff, "diff can not null");
        Object valueOld = ReflectionUtils.getField(field, docInfoOld);
        Object valueNew = ReflectionUtils.getField(field, docInfoNew);
        if (valueOld instanceof List && valueNew instanceof List) {
            List<DocParamDTO> docParamDTOListOld = (List<DocParamDTO>) valueOld;
            List<DocParamDTO> docParamDTOListNew = (List<DocParamDTO>) valueNew;
            if (CollectionUtils.isEmpty(docParamDTOListOld) && CollectionUtils.isEmpty(docParamDTOListNew)) {
                return Collections.emptyList();
            }
            docParamDTOListOld = flatParams(docParamDTOListOld);
            docParamDTOListNew = flatParams(docParamDTOListNew);
            return buildDocParamDiffBeans(diff, docParamDTOListOld, docParamDTOListNew);
        }
        ModifyType modifyType = buildModifyType(valueOld, valueNew);
        DiffBean diffBean = new DiffBean();
        diffBean.setTargetName(name);
        diffBean.setPositionType(diff.positionType());
        diffBean.setOldValue(ofString(valueOld));
        diffBean.setNewValue(ofString(valueNew));
        diffBean.setModifyType(modifyType);
        return Collections.singletonList(diffBean);
    }

    private static List<DocParamDTO> flatParams(List<DocParamDTO> docParamDTOS) {
        List<DocParamDTO> ret = new ArrayList<>(docParamDTOS.size());
        doFlatParams(ret, docParamDTOS);
        return ret;
    }

    private static void doFlatParams(List<DocParamDTO> ret, List<DocParamDTO> docParamDTOS) {
        for (DocParamDTO param : docParamDTOS) {
            ret.add(param);
            List<DocParamDTO> children = param.getChildren();
            if (!CollectionUtils.isEmpty(children)) {
                doFlatParams(ret, children);
            }
        }
    }

    private static List<DiffBean> buildDocParamDiffBeans(Diff diff, List<DocParamDTO> docParamDTOListOld, List<DocParamDTO> docParamDTOListNew) {
        List<DiffBean> diffBeans = new ArrayList<>();
        // key：字段名称
        Map<String, DocParamDTO> paramOldMap = docParamDTOListOld.stream()
                .collect(Collectors.toMap(DocParamDTO::getName, Function.identity(), (v1, v2) -> v2, LinkedHashMap::new));

        Map<String, DocParamDTO> paramNewMap = docParamDTOListNew.stream()
                .collect(Collectors.toMap(DocParamDTO::getName, Function.identity(), (v1, v2) -> v2, LinkedHashMap::new));

        // 修改过的
        List<String> updateList = new ArrayList<>(paramOldMap.keySet());
        // 移除的
        List<String> removeList = new ArrayList<>(paramOldMap.keySet());
        // 新增的
        List<String> addList = new ArrayList<>(paramNewMap.keySet());

        // 交集部分就是修改部分
        updateList.retainAll(paramNewMap.keySet());
        // 老的移除新的，剩下的就是要删除的
        removeList.removeAll(paramNewMap.keySet());
        // 新的移除老的，剩下就是新增的
        addList.removeAll(paramOldMap.keySet());

        List<DiffBean> diffBeanUpdateList = buildParamDiffBeans(diff, ModifyType.UPDATE, paramOldMap, paramNewMap, updateList);
        List<DiffBean> diffBeanRemoveList = buildParamDiffBeans(diff, ModifyType.DELETE, paramOldMap, paramNewMap, removeList);
        List<DiffBean> diffBeanAddList = buildParamDiffBeans(diff, ModifyType.ADD, paramOldMap, paramNewMap, addList);

        diffBeans.addAll(diffBeanUpdateList);
        diffBeans.addAll(diffBeanRemoveList);
        diffBeans.addAll(diffBeanAddList);

        return diffBeans;
    }

    private static List<DiffBean> buildParamDiffBeans(
            Diff diff,
            ModifyType modifyType,
            Map<String, DocParamDTO> paramOldMap,
            Map<String, DocParamDTO> paramNewMap,
            List<String> names) {
        if (CollectionUtils.isEmpty(names)) {
            return Collections.emptyList();
        }
        List<DiffBean> diffBeans = new ArrayList<>(names.size());
        for (String name : names) {
            DocParamDTO docParamOld = paramOldMap.get(name);
            DocParamDTO docParamNew = paramNewMap.get(name);
            Map<String, Object> valueOld = buildParamValue(docParamOld);
            Map<String, Object> valueNew = buildParamValue(docParamNew);

            removeSaveValue(valueOld, valueNew);
            // 如果都为空，表示没有改动
            if (valueOld.isEmpty() && valueNew.isEmpty()) {
                continue;
            }

            DiffBean diffBean = new DiffBean();
            diffBean.setTargetName(name);
            diffBean.setModifyType(modifyType);
            diffBean.setOldValue(valueOld);
            diffBean.setNewValue(valueNew);
            diffBean.setPositionType(diff.positionType());
            diffBeans.add(diffBean);
        }
        return diffBeans;
    }

    private static void removeSaveValue(Map<String, Object> valueOld, Map<String, Object> valueNew) {
        List<String> sameKeys = new ArrayList<>();
        for (Map.Entry<String, Object> entry : valueOld.entrySet()) {
            String key = entry.getKey();
            Object valOld = entry.getValue();
            Object valNew = valueNew.get(key);
            if (Objects.equals(valOld, valNew)) {
                sameKeys.add(key);
            }
        }
        for (String key : sameKeys) {
            valueOld.remove(key);
            valueNew.remove(key);
        }
    }

    private static Map<String, Object> buildParamValue(DocParamDTO docParamDTO) {
        if (docParamDTO == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> valueMap = new LinkedHashMap<>(16);
        ReflectionUtils.doWithFields(docParamDTO.getClass(), field -> {
            field.setAccessible(true);
            Diff diff = AnnotationUtils.getAnnotation(field, Diff.class);
            Object value = ReflectionUtils.getField(field, docParamDTO);
            valueMap.put(diff.positionType().getTypeName(), value);
        }, field -> AnnotationUtils.getAnnotation(field, Diff.class) != null);
        return valueMap;
    }

    private static ModifyType buildModifyType(Object valueOld, Object valueNew) {
        if (valueOld == null && valueNew != null) {
            return ModifyType.ADD;
        } else if (valueOld != null && valueNew == null) {
            return ModifyType.DELETE;
        } else if (valueOld != null && valueNew != null && !Objects.equals(valueOld, valueNew)) {
            return ModifyType.UPDATE;
        } else {
            return ModifyType.NONE;
        }
    }


    private static String ofString(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    @Data
    private static class FieldBean {
        private PositionType positionType;
        private String value;
    }

    @Data
    private static class DiffBean {

        private String targetName;

        private PositionType positionType;

        private ModifyType modifyType;

        private Object oldValue;

        private Object newValue;
    }


    @Data
    @AllArgsConstructor
    private static class OldNewValue {
        private Object oldValue;
        private Object newValue;
    }

}