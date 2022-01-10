package cn.torna.common.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author tanghc
 */
public class DataIdUtil {

    private static final String TPL_PARAM = "%s:%s:%s:%s";
    private static final String TPL_ENUM = "%s:%s";

    /**
     * 文档参数唯一id，md5(doc_id:parent_id:style:name)
     * @param docId 文档id
     * @param parentId 父id
     * @param style 类型
     * @param name 文档名称
     * @return
     */
    public static String getDocParamDataId(long docId, Long parentId, byte style, String name) {
        if (name == null) {
            name = "";
        }
        if (parentId == null) {
            parentId = 0L;
        }
        String content = String.format(TPL_PARAM, docId, parentId, style, name);
        return DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 枚举唯一id
     * @param moduleId 模块id
     * @param name 名称
     * @return
     */
    public static String getEnumInfoDataId(long moduleId, String name) {
        String content = String.format(TPL_ENUM, moduleId, name);
        return DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8));
    }

}
