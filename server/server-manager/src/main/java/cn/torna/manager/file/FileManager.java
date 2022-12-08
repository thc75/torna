package cn.torna.manager.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author thc
 */
public interface FileManager {
    /**
     * 保存文件
     * @param file 文件
     * @return 返回文件路径
     */
    String save(MultipartFile file) throws IOException;
}
