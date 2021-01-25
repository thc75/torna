package cn.torna.service.dataid;

import cn.torna.common.bean.Booleans;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 唯一id，接口规则：md5(module_id:parent_id:url:http_method)。
 * 分类规则：md5(module_id:parent_id:name)
 * @author tanghc
 */
public interface DocInfoDataId {

    String TPL_API = "%s:%s:%s:%s";
    String TPL_FOLDER = "%s:%s:%s";

    default String buildDataId() {
        Long parentId = getParentId();
        if (parentId == null) {
            parentId = 0L;
        }
        String content =  Booleans.isTrue(this.getIsFolder())?
                String.format(TPL_FOLDER, getModuleId(), parentId, getName()) :
                String.format(TPL_API, getModuleId(), parentId, getUrl(), getHttpMethod());
        return DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8));
    }

    String getName();

    Long getModuleId();

    Long getParentId();

    Byte getIsFolder();

    String getUrl();

    String getHttpMethod();

}
