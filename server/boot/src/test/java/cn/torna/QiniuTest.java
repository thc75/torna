package cn.torna;

import cn.torna.manager.file.QiNiuClient;
import cn.torna.manager.file.QiNiuKodoFileManager;
import cn.torna.manager.file.QiNiuKodoPropertiesUtils;
import com.qiniu.common.QiniuException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author thc
 */
public class QiniuTest extends TornaApplicationTests {


    @Autowired
    QiNiuKodoFileManager qiNiuKodoFileManager;

    @Test
    public void save() throws IOException {
        try {
            File file = new File("C:\\Users\\thc\\Pictures\\1843036242e2b742f06.jpg");
            MockMultipartFile multipartFile = new MockMultipartFile("1843036242e2b742f06", "1843036242e2b742f06.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    FileUtils.openInputStream(file));
            String url = qiNiuKodoFileManager.save(multipartFile);
            System.out.println(url);
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }
    }
}
