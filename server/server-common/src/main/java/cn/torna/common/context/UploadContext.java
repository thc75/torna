package cn.torna.common.context;

import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.util.SystemUtil;
import org.springframework.util.StringUtils;

/**
 * @author thc
 */
public class UploadContext {
    public static final String MAPPING_PREFIX = "/upload";
    public static final String MAPPING = MAPPING_PREFIX + "/**";

    public static String getUploadDir() {
        String dir = EnvironmentKeys.TORNA_UPLOAD_DIR.getValue();
        if (StringUtils.isEmpty(dir)) {
            dir = SystemUtil.getUserHome() + "/torna_upload";
        }
        return StringUtils.trimTrailingCharacter(dir, '/');
    }

}
