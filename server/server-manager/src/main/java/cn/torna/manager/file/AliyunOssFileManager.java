package cn.torna.manager.file;

import cn.torna.common.context.UploadContext;
import cn.torna.common.exception.BizException;
import com.aliyun.oss.OSS;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 阿里云oss文件上传
 *
 * @author thc
 */
@Service
public class AliyunOssFileManager implements FileManager {

    @Override
    public String save(MultipartFile file) throws IOException {
        OSS ossClient = AliyunOssPropertiesUtils.getOssClient();
        if (ossClient == null) {
            throw new BizException("阿里云OSS客户端创建失败");
        }
        String endpoint = AliyunOssPropertiesUtils.getEndPoint();
        String bucketName = AliyunOssPropertiesUtils.getBucketName();

        // 获取上传文件输入流
        InputStream inputStream = file.getInputStream();
        // 2022/12/7/xxx.png
        String path = UploadContext.getPath(file);

        // 调用oss方法上传到阿里云
        ossClient.putObject(bucketName, path, inputStream);

        // 上传后把文件url返回
        return "https://" + bucketName + "." + endpoint + "/" + path;
    }

}
