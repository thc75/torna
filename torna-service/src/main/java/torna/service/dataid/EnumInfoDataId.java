package torna.service.dataid;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author tanghc
 */
public interface EnumInfoDataId {

    String TPL = "%s:%s";

    /**
     * 唯一id，md5(module_id:name)
     * @return
     */
    default String buildDataId() {
        String content = String.format(TPL, getModuleId(), getName());
        return DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8));
    }

    Long getModuleId();

    String getName();

}
