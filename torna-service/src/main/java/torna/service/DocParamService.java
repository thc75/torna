package torna.service;

import torna.common.bean.Booleans;
import torna.common.context.DocConstants;
import torna.common.support.BaseService;
import torna.dao.entity.DocParam;
import torna.dao.mapper.DocParamMapper;
import com.gitee.fastmybatis.core.util.MyBeanUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author tanghc
 */
@Service
public class DocParamService extends BaseService<DocParam, DocParamMapper> {

    public DocParam getByUniqueId(String uniqueId) {
        return get("unique_id", uniqueId);
    }

    public DocParam saveDoc(DocParam docParam) {
        DocParam docParamExist = getByUniqueId(docParam.getUniqueId());
        if (docParamExist != null) {
            MyBeanUtil.copyPropertiesIgnoreNull(docParam, docParamExist);
            docParamExist.setIsDeleted(Booleans.FALSE);
            updateIgnoreNull(docParamExist);
            return docParamExist;
        } else {
            saveIgnoreNull(docParam);
            return docParam;
        }
    }

    /**
     * 唯一id,md5(name+doc_id+parent_id)
     * @return
     */
    public static String buildUniqueId(String name, long docId, long parentId) {
        String content = String.format(DocConstants.UNIQUE_ID_TPL, name, docId, parentId);
        return DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8));
    }

}
