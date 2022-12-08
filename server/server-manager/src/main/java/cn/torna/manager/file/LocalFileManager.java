package cn.torna.manager.file;

import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.context.UploadContext;
import cn.torna.common.util.IdGen;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * @author thc
 */
@Service
public class LocalFileManager implements FileManager {

    private static final String PATH_FORMAT = "%s/%s.%s";

    /**
     * 保存文件
     * @param file
     * @return 返回访问路径
     * @throws IOException
     */
    @Override
    public String save(MultipartFile file) throws IOException {
        String uploadDir = UploadContext.getUploadDir();
        // 2022/12/6
        String folder = UploadContext.getSaveFolder();
        // 创建保存目录
        FileUtils.forceMkdir(new File(uploadDir + "/" + folder));
        // 2022/12/6/XXXX.jpg
        String path = String.format(PATH_FORMAT, folder, IdGen.uuid(), UploadContext.getExtension(file.getOriginalFilename()));
        file.transferTo(new File(uploadDir + "/" + path));
        String domain = EnvironmentKeys.TORNA_UPLOAD_DOMAIN.getValue();
        if (StringUtils.isEmpty(domain)) {
            domain = UploadContext.MAPPING_PREFIX;
        }
        return StringUtils.trimTrailingCharacter(domain, '/')  + "/" + path;
    }

    public File getFile(String path) {
        String mappingPrefix = UploadContext.MAPPING_PREFIX;
        if (path.startsWith(mappingPrefix)) {
            // /2022/12/6/8c99c595c4804734a018b21c3f0c02ba.png
            path = path.substring(mappingPrefix.length());
        }
        String uploadDir = UploadContext.getUploadDir();
        return new File(uploadDir + "/" + StringUtils.trimLeadingCharacter(path, '/'));
    }






}
