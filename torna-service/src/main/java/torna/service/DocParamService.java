package torna.service;

import com.gitee.fastmybatis.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import torna.common.bean.Booleans;
import torna.common.context.DocConstants;
import torna.common.support.BaseService;
import torna.dao.entity.DocParam;
import torna.dao.mapper.DocParamMapper;

import java.nio.charset.StandardCharsets;

/**
 * @author tanghc
 */
@Service
public class DocParamService extends BaseService<DocParam, DocParamMapper> {

    public DocParam getByUniqueId(String uniqueId) {
        Query query = new Query()
                .eq("unique_id", uniqueId)
                .enableForceQuery();
        return this.get(query);
    }

    public DocParam saveDoc(DocParam docParam) {
        DocParam docParamExist = getByUniqueId(docParam.getUniqueId());
        if (docParamExist != null) {
            // name, docId, parentId 三项不用改
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

    /**
     * 唯一id,md5(name:doc_id:parent_id)
     * @return
     */
    public static String buildUniqueId(String name, long docId, long parentId) {
        String content = String.format(DocConstants.UNIQUE_ID_TPL, name, docId, parentId);
        return DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8));
    }

}
