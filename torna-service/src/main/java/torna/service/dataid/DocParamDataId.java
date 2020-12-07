package torna.service.dataid;

import torna.common.util.DataIdUtil;

/**
 * 唯一id，md5(doc_id:parent_id:style:name)
 * @author tanghc
 */
public interface DocParamDataId {

    /**
     * 唯一id，md5(doc_id:parent_id:style:name)
     * @return
     */
    default String buildDataId() {
        return DataIdUtil.getDocParamDataId(getDocId(), getParentId(), getStyle(), getName());
    }

    Long getDocId();

    String getName();

    Long getParentId();

    Byte getStyle();

}
