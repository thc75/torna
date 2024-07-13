package cn.torna.service;

import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.PushIgnoreField;
import cn.torna.dao.mapper.PushIgnoreFieldMapper;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import org.springframework.stereotype.Service;


/**
 * @author tanghc
 */
@Service
public class PushIgnoreFieldService extends BaseLambdaService<PushIgnoreField, PushIgnoreFieldMapper> {

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
        Query query = this.query()
                .eq(PushIgnoreField::getModuleId, moduleId)
                .eq(PushIgnoreField::getDataId, dataId)
                .eq(PushIgnoreField::getFieldName, fieldName);
        return get(query);
    }

    public boolean isPushIgnore(long moduleId, String dataId, String fieldName) {
        return getByDataIdAndFieldName(moduleId, dataId, fieldName) != null;
    }

}
