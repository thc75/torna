package cn.torna.service;

import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.PushIgnoreField;
import cn.torna.dao.mapper.PushIgnoreFieldMapper;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.stereotype.Service;


/**
 * @author tanghc
 */
@Service
public class PushIgnoreFieldService extends BaseService<PushIgnoreField, PushIgnoreFieldMapper> {

    public void addField(DocInfo docInfo, String fieldName, String fieldDescription) {
        PushIgnoreField pushIgnoreField = getByDataIdAndFieldName(docInfo.getModuleId(), docInfo.getDataId(), fieldName);
        if (pushIgnoreField != null) {
            return;
        }
        pushIgnoreField = new PushIgnoreField();
        pushIgnoreField.setFieldName(fieldName);
        pushIgnoreField.setDataId(docInfo.getDataId());
        pushIgnoreField.setModuleId(docInfo.getModuleId());
        pushIgnoreField.setFieldDescription(fieldDescription);
        this.save(pushIgnoreField);
    }

    public PushIgnoreField getByDataIdAndFieldName(long moduleId, String dataId, String fieldName) {
        Query query = new Query()
                .eq("module_id", moduleId)
                .eq("data_id", dataId)
                .eq("field_name", fieldName);
        return get(query);
    }

    public boolean isPushIgnore(long moduleId, String dataId, String fieldName) {
        return getByDataIdAndFieldName(moduleId, dataId, fieldName) != null;
    }

}