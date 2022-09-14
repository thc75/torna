package cn.torna.service;

import cn.torna.common.annotation.Diff;
import cn.torna.common.enums.ModifyType;
import cn.torna.common.enums.PositionType;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.IdGen;
import cn.torna.dao.entity.DocDiffRecord;
import cn.torna.dao.entity.DocSnapshot;
import cn.torna.dao.mapper.DocDiffRecordMapper;
import cn.torna.service.dto.DocDiffDTO;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.SaveDiffDTO;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;


/**
 * @author tanghc
 */
@Service
public class DocDiffRecordService extends BaseService<DocDiffRecord, DocDiffRecordMapper> {

    @Autowired
    private DocSnapshotService docSnapshotService;

    public void processDocDiff(DocDiffDTO docDiffDTO) {
        DocInfoDTO docInfoNew = docDiffDTO.getDocInfoDTO();
        // 保存新快照
        docSnapshotService.saveDocSnapshot(docInfoNew);
        // 根据旧的md5，找出老文档内容
        String md5Old = docDiffDTO.getMd5Old();
        DocSnapshot docSnapshot = docSnapshotService.getByMd5(md5Old);
        if (docSnapshot == null) {
            return;
        }
        String content = docSnapshot.getContent();
        DocInfoDTO docInfoOld = JSON.parseObject(content, DocInfoDTO.class);
        this.doCompare(docInfoOld, docInfoNew);
    }

    public void doCompare(DocInfoDTO docInfoOld, DocInfoDTO docInfoNew) {
        long operateKey = IdGen.genId();
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

        List<String> updateList = new ArrayList<>(oldDocInfoMap.keySet());
        List<String> removeList = new ArrayList<>(oldDocInfoMap.keySet());
        List<String> addList = new ArrayList<>(newDocInfoMap.keySet());
        updateList.retainAll(newDocInfoMap.keySet());
        removeList.removeAll(newDocInfoMap.keySet());
        addList.removeAll(oldDocInfoMap.keySet());
        // 修改
        for (String name : updateList) {
            FieldBean fieldBeanOld = oldDocInfoMap.get(name);
            FieldBean fieldBeanNew = newDocInfoMap.get(name);
            this.saveDiff(Objects.equals(fieldBeanOld.getValue(), fieldBeanNew.getValue()), () -> {
                return SaveDiffDTO.builder()
                        .operateKey(operateKey)
                        .dataId(docInfoNew.buildDataId())
                        .targetName(name)
                        .modifyType(ModifyType.UPDATE)
                        .positionType(fieldBeanNew.getPositionType())
                        .oldValue(fieldBeanOld.getValue())
                        .newValue(fieldBeanNew.getValue())
                        .build();
            });
        }
        // 删除
        for (String name : removeList) {
            FieldBean fieldBeanOld = oldDocInfoMap.get(name);
            this.saveDiff(false, () -> {
                return SaveDiffDTO.builder()
                        .operateKey(operateKey)
                        .dataId(docInfoOld.buildDataId())
                        .targetName(name)
                        .modifyType(ModifyType.DELETE)
                        .positionType(fieldBeanOld.getPositionType())
                        .oldValue(fieldBeanOld.getValue())
                        .newValue("")
                        .build();
            });
        }
        // 新增
        for (String name : addList) {
            FieldBean fieldBeanNew = newDocInfoMap.get(name);
            this.saveDiff(false, () -> {
                return SaveDiffDTO.builder()
                        .operateKey(operateKey)
                        .dataId(docInfoOld.buildDataId())
                        .targetName(name)
                        .modifyType(ModifyType.ADD)
                        .positionType(fieldBeanNew.getPositionType())
                        .oldValue("")
                        .newValue(fieldBeanNew.getValue())
                        .build();
            });
        }
        // TODO: 比较参数部分
    }

    private static FieldBean buildFieldBean(Field field, DocInfoDTO docInfoDTO) {
        field.setAccessible(true);
        Object value = ReflectionUtils.getField(field, docInfoDTO);
        if (value == null || StringUtils.isEmpty(String.valueOf(value))) {
            return null;
        }
        Diff diff = AnnotationUtils.getAnnotation(field, Diff.class);
        FieldBean fieldBean = new FieldBean();
        fieldBean.setPositionType(diff.positionType());
        fieldBean.setValue(String.valueOf(value));
        return fieldBean;
    }

    public void saveDiff(boolean isSame, Supplier<SaveDiffDTO> saveDiffDTOSupplier) {
        if (!isSame) {
            SaveDiffDTO saveDiffDTO = saveDiffDTOSupplier.get();
            DocDiffRecord docDiffRecord = CopyUtil.copyBean(saveDiffDTO, DocDiffRecord::new);
            docDiffRecord.setPositionType(saveDiffDTO.getPositionType().getType());
            docDiffRecord.setModifyType(saveDiffDTO.getModifyType().getType());
            this.save(docDiffRecord);
        }
    }

    @Data
    private static class FieldBean {
        private PositionType positionType;
        private String value;
    }

}