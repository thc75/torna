package cn.torna.service;
import com.google.common.collect.Lists;
import cn.torna.service.dto.EnumInfoDTO;
import java.time.LocalDateTime;

import cn.torna.common.annotation.Diff;
import cn.torna.common.bean.Booleans;
import cn.torna.common.enums.ModifyType;
import cn.torna.common.enums.PositionType;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.IdGen;
import cn.torna.dao.entity.DocDiffDetail;
import cn.torna.dao.entity.DocDiffRecord;
import cn.torna.dao.mapper.DocDiffDetailMapper;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocParamDTO;
import cn.torna.service.dto.SaveDiffDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * @author tanghc
 */
@Service
public class DocDiffDetailService extends BaseService<DocDiffDetail, DocDiffDetailMapper> {

    private static final ThreadLocal<String> modifyNicknameLocal = new ThreadLocal<>();

    /**
     * 比较两个文档对象不同的地方
     *
     * @param docInfoOld     老文档
     * @param docInfoNew     新文档
     * @param modifyNickname 修改人
     */
    public void doCompare(DocInfoDTO docInfoOld, DocInfoDTO docInfoNew, DocDiffRecord docDiffRecord, String modifyNickname) {
        Long recordId = docDiffRecord.getId();
        modifyNicknameLocal.set(modifyNickname);
        Map<String, FieldBean> oldDocInfoMap = new LinkedHashMap<>();
        Map<String, FieldBean> newDocInfoMap = new LinkedHashMap<>();

        ReflectionUtils.doWithFields(docInfoOld.getClass(), field -> {
            String name = field.getName();
            FieldBean fieldBeanOld = buildFieldBean(field, docInfoOld);
            FieldBean fieldBeanNew = buildFieldBean(field, docInfoNew);
            if (fieldBeanOld != null) {
                oldDocInfoMap.put(name, fieldBeanOld);
            }
            if (fieldBeanNew != null) {
                newDocInfoMap.put(name, fieldBeanNew);
            }
        }, field -> AnnotationUtils.getAnnotation(field, Diff.class) != null);
        // 修改过的
        List<String> updateList = new ArrayList<>(oldDocInfoMap.keySet());
        // 移除的
        List<String> removeList = new ArrayList<>(oldDocInfoMap.keySet());
        // 新增的
        List<String> addList = new ArrayList<>(newDocInfoMap.keySet());
        // 交集部分就是修改部分
        updateList.retainAll(newDocInfoMap.keySet());
        // 老的移除新的，剩下的就是要删除的
        removeList.removeAll(newDocInfoMap.keySet());
        // 新的移除老的，剩下就是新增的
        addList.removeAll(oldDocInfoMap.keySet());
        // 修改
        for (String name : updateList) {
            FieldBean fieldBeanOld = oldDocInfoMap.get(name);
            FieldBean fieldBeanNew = newDocInfoMap.get(name);
            this.saveDiff(Objects.equals(fieldBeanOld.getValue(), fieldBeanNew.getValue()), () -> {
                return SaveDiffDTO.builder()
                        .recordId(recordId)
                        .targetName(name)
                        .modifyType(ModifyType.UPDATE)
                        .positionType(fieldBeanNew.getPositionType())
                        .oldValue(fieldBeanOld.getValue())
                        .newValue(fieldBeanNew.getValue())
                        .modifyNickname(modifyNickname)
                        .build();
            });
        }
        // 删除
        for (String name : removeList) {
            FieldBean fieldBeanOld = oldDocInfoMap.get(name);
            this.saveDiff(false, () -> {
                return SaveDiffDTO.builder()
                        .recordId(recordId)
                        .targetName(name)
                        .modifyType(ModifyType.DELETE)
                        .positionType(fieldBeanOld.getPositionType())
                        .oldValue(fieldBeanOld.getValue())
                        .newValue("")
                        .modifyNickname(modifyNickname)
                        .build();
            });
        }
        // 新增
        for (String name : addList) {
            FieldBean fieldBeanNew = newDocInfoMap.get(name);
            this.saveDiff(false, () -> {
                return SaveDiffDTO.builder()
                        .recordId(recordId)
                        .targetName(name)
                        .modifyType(ModifyType.ADD)
                        .positionType(fieldBeanNew.getPositionType())
                        .oldValue("")
                        .newValue(fieldBeanNew.getValue())
                        .modifyNickname(modifyNickname)
                        .build();
            });
        }
        // 对比header
        this.compareParam(docDiffRecord, docInfoOld, docInfoNew, DocInfoDTO::getHeaderParams);
        // 对比路径参数
        this.compareParam(docDiffRecord, docInfoOld, docInfoNew, DocInfoDTO::getPathParams);
        // 对比查询参数
        this.compareParam(docDiffRecord, docInfoOld, docInfoNew, DocInfoDTO::getQueryParams);
        // 对比请求体参数
        this.compareParam(docDiffRecord, docInfoOld, docInfoNew, docInfoDTO -> getAllParams(docInfoDTO, DocInfoDTO::getRequestParams));
        // 对比返回参数
        this.compareParam(docDiffRecord, docInfoOld, docInfoNew, docInfoDTO -> getAllParams(docInfoDTO, DocInfoDTO::getResponseParams));

        modifyNicknameLocal.remove();
    }

    private List<DocParamDTO> getAllParams(DocInfoDTO docInfoDTO, Function<DocInfoDTO, List<DocParamDTO>> apply) {
        List<DocParamDTO> docParamDTOList = apply.apply(docInfoDTO);
        return getAllParams(docParamDTOList);
    }

    private List<DocParamDTO> getAllParams(List<DocParamDTO> docInfoDTOList) {
        if (CollectionUtils.isEmpty(docInfoDTOList)) {
            return Collections.emptyList();
        }
        List<DocParamDTO> list = new ArrayList<>();
        for (DocParamDTO docParamDTO : docInfoDTOList) {
            DocParamDTO item = new DocParamDTO();
            item.setName(docParamDTO.getName());
            item.setType(docParamDTO.getType());
            item.setRequired(docParamDTO.getRequired());
            item.setMaxLength(docParamDTO.getMaxLength());
            item.setExample(docParamDTO.getExample());
            item.setDescription(docParamDTO.getDescription());
            item.setEnumId(docParamDTO.getEnumId());
            item.setDocId(docParamDTO.getDocId());
            list.add(item);
            List<DocParamDTO> children = docParamDTO.getChildren();
            if (!CollectionUtils.isEmpty(children)) {
                List<DocParamDTO> allParams = getAllParams(children);
                list.addAll(allParams);
            }
        }
        return list;
    }

    private void compareParam(DocDiffRecord docDiffRecord, DocInfoDTO docInfoOld, DocInfoDTO docInfoNew, Function<DocInfoDTO, List<DocParamDTO>> apply) {
        List<DocParamDTO> headerParamsOld = Optional.ofNullable(apply.apply(docInfoOld)).orElse(Collections.emptyList());
        List<DocParamDTO> headerParamsNew = Optional.ofNullable(apply.apply(docInfoNew)).orElse(Collections.emptyList());
        // key：字段名称
        Map<String, DocParamDTO> headerOldMap = headerParamsOld.stream()
                .collect(Collectors.toMap(DocParamDTO::getName, Function.identity()));

        Map<String, DocParamDTO> headerNewMap = headerParamsNew.stream()
                .collect(Collectors.toMap(DocParamDTO::getName, Function.identity()));

        // 修改过的
        List<String> updateList = new ArrayList<>(headerOldMap.keySet());
        // 移除的
        List<String> removeList = new ArrayList<>(headerOldMap.keySet());
        // 新增的
        List<String> addList = new ArrayList<>(headerNewMap.keySet());

        // 交集部分就是修改部分
        updateList.retainAll(headerNewMap.keySet());
        // 老的移除新的，剩下的就是要删除的
        removeList.removeAll(headerNewMap.keySet());
        // 新的移除老的，剩下就是新增的
        addList.removeAll(headerOldMap.keySet());

        // 修改
        compareParamDetail(docDiffRecord, docInfoNew.buildDataId(), headerOldMap, headerNewMap, ModifyType.UPDATE, updateList);
        // 删除
        compareParamDetail(docDiffRecord, docInfoNew.buildDataId(), headerOldMap, headerNewMap, ModifyType.DELETE, removeList);
        // 新增
        compareParamDetail(docDiffRecord, docInfoNew.buildDataId(), headerOldMap, headerNewMap, ModifyType.ADD, addList);

    }

    private void compareParamDetail(
            DocDiffRecord docDiffRecord,
            String dataId,
            Map<String, DocParamDTO> headerOldMap,
            Map<String, DocParamDTO> headerNewMap,
            ModifyType modifyType,
            List<String> list

    ) {
        for (String name : list) {
            DocParamDTO paramOld = headerOldMap.get(name);
            DocParamDTO paramNew = headerNewMap.get(name);
            this.saveParamTypeDiff(docDiffRecord,
                    dataId,
                    paramOld,
                    paramNew,
                    modifyType,
                    Arrays.asList(
                            new ParamOperate(PositionType.PARAM_TYPE, DocParamDTO::getType),
                            new ParamOperate(PositionType.PARAM_REQUIRED, docParamDTO -> String.valueOf(Booleans.isTrue(docParamDTO.getRequired()))),
                            new ParamOperate(PositionType.PARAM_DESCRIPTION, DocParamDTO::getDescription),
                            new ParamOperate(PositionType.PARAM_MAXLENGTH, DocParamDTO::getMaxLength),
                            new ParamOperate(PositionType.PARAM_EXAMPLE, DocParamDTO::getExample)
                    )
            );
        }
    }


    private void saveParamTypeDiff(
            DocDiffRecord docDiffRecord,
            String docDataId,
            DocParamDTO paramOld,
            DocParamDTO paramNew,
            ModifyType modifyType,
            List<ParamOperate> paramOperates
    ) {

        String modifyNickname = modifyNicknameLocal.get();
        for (ParamOperate paramOperate : paramOperates) {
            Function<DocParamDTO, String> valueGetter = paramOperate.getValueGetter();
            PositionType positionType = paramOperate.getPositionType();
            String oldVal = paramOld == null ? "" : valueGetter.apply(paramOld);
            String newVal = paramNew == null ? "" : valueGetter.apply(paramNew);
            this.saveDiff(Objects.equals(oldVal, newVal), () -> {
                return SaveDiffDTO.builder()
                        .recordId(docDiffRecord.getId())
                        .targetName(positionType.getTypeName())
                        .modifyType(modifyType)
                        .positionType(positionType)
                        .oldValue(oldVal)
                        .newValue(newVal)
                        .modifyNickname(modifyNickname)
                        .build();
            });
        }

    }

    private static FieldBean buildFieldBean(Field field, DocInfoDTO docInfoDTO) {
        field.setAccessible(true);
        Object value = ReflectionUtils.getField(field, docInfoDTO);
        if (value == null || StringUtils.isEmpty(String.valueOf(value))) {
            return null;
        }
        Diff diff = AnnotationUtils.getAnnotation(field, Diff.class);
        if (diff == null) {
            return null;
        }
        FieldBean fieldBean = new FieldBean();
        fieldBean.setPositionType(diff.positionType());
        fieldBean.setValue(String.valueOf(value));
        return fieldBean;
    }

    /**
     * 保存不同的地方
     *
     * @param isSame              是否一样
     * @param saveDiffDTOSupplier 如果不一样执行操作
     */
    public void saveDiff(boolean isSame, Supplier<SaveDiffDTO> saveDiffDTOSupplier) {
        if (!isSame) {
            SaveDiffDTO saveDiffDTO = saveDiffDTOSupplier.get();
            DocDiffDetail docDiffRecord = CopyUtil.copyBean(saveDiffDTO, DocDiffDetail::new);
            docDiffRecord.setPositionType(saveDiffDTO.getPositionType().getType());
            docDiffRecord.setModifyType(saveDiffDTO.getModifyType().getType());
            this.save(docDiffRecord);
        }
    }

    @Data
    @AllArgsConstructor
    private static class ParamOperate {
        private PositionType positionType;
        Function<DocParamDTO, String> valueGetter;
    }

    @Data
    private static class FieldBean {
        private PositionType positionType;
        private String value;
    }

}