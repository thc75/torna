package torna.common.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author tanghc
 */
public class DataIdUtil {

    private static final String TPL_PARAM = "%s:%s:%s:%s";

    /**
     * 唯一id，md5(doc_id:parent_id:style:name)
     * @return
     */
    public static String getDocParamDataId(long docId, long parentId, byte style, String name) {
        String content = String.format(TPL_PARAM, docId, parentId, style, name);
        return DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8));
    }

}
