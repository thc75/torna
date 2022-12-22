package cn.torna.common.context;

import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.util.IdGen;
import cn.torna.common.util.SystemUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * @author thc
 */
public class UploadContext {

    private static final String PATH_FORMAT = "%s/%s.%s";
    public static final String MAPPING_PREFIX = "/upload";
    public static final String MAPPING = MAPPING_PREFIX + "/**";

    public static String getUploadDir() {
        String dir = EnvironmentKeys.TORNA_UPLOAD_DIR.getValue();
        if (StringUtils.isEmpty(dir)) {
            dir = SystemUtil.getUserHome() + "/torna_upload";
        }
        return StringUtils.trimTrailingCharacter(dir, '/');
    }

    public static String getSaveFolder() {
        LocalDate now = LocalDate.now();
        List<String> strings = Arrays.asList(
                String.valueOf(now.getYear()),
                String.valueOf(now.getMonthValue()),
                String.valueOf(now.getDayOfMonth())
        );
        return String.join("/", strings);
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return "jpg";
        }
        int index = filename.lastIndexOf('.');
        if (index > -1) {
            return filename.substring(index + 1);
        }
        return filename;
    }

    public static String getPath(String folder, MultipartFile file) {
        return String.format(PATH_FORMAT, folder, IdGen.uuid(), UploadContext.getExtension(file.getOriginalFilename()));
    }

    public static String getPath(MultipartFile file) {
        String folder = getSaveFolder();
        return String.format(PATH_FORMAT, folder, IdGen.uuid(), UploadContext.getExtension(file.getOriginalFilename()));
    }

}
