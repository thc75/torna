package cn.torna.manager.file;

import cn.torna.common.context.UploadContext;
import cn.torna.common.exception.BizException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author thc
 */
@Service
public class QiNiuKodoFileManager implements FileManager {
    @Override
    public String save(MultipartFile file) throws IOException {
        QiNiuClient qiNiuClient = QiNiuKodoPropertiesUtils.getQiNiuClient();
        if (qiNiuClient == null) {
            throw new BizException("七牛云客户端创建失败");
        }
        // 2022/12/7/xxx.png
        String path = UploadContext.getPath(file);
        return qiNiuClient.put(file.getInputStream(), path);
    }
}
