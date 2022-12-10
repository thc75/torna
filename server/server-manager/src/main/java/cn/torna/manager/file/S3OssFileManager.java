package cn.torna.manager.file;

import cn.torna.common.context.UploadContext;
import cn.torna.common.exception.BizException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 阿里云oss文件上传
 *
 * @author thc
 */
@Service
public class S3OssFileManager implements FileManager {

    @Autowired
    private S3OssPropertiesUtils s3OssPropertiesUtils;

    @Override
    public String save(MultipartFile file) throws IOException {
        AmazonS3 ossClient = s3OssPropertiesUtils.getAmazonS3();
        if (ossClient == null) {
            throw new BizException("S3 OSS客户端创建失败");
        }
        String bucketName = s3OssPropertiesUtils.getBucketName();

        // 获取上传文件输入流
        InputStream inputStream = file.getInputStream();
        // 2022/12/7/xxx.png
        String path = UploadContext.getPath(file);

        // 调用oss方法上传到oss
        putObject(bucketName, path, inputStream, file.getContentType());

        // 上传后把文件url返回
        return getUrl(s3OssPropertiesUtils, path);
    }

    /**
     * 返回图片访问地址
     * @param s3OssPropertiesUtils 配置
     * @param path 短路径，如：2022/12/4/xxx.jpg
     * @return 返回完整的url
     */
    public String getUrl(S3OssPropertiesUtils s3OssPropertiesUtils, String path) {
        // https://docs.aws.amazon.com/AmazonS3/latest/userguide/VirtualHosting.html?spm=a2c4g.11186623.0.0.6b9d2041QVyFGH
        String domainPattern = s3OssPropertiesUtils.getDomainPattern();
        domainPattern = domainPattern.replace("<endpoint>", s3OssPropertiesUtils.getEndpoint());
        domainPattern = domainPattern.replace("<bucketName>", s3OssPropertiesUtils.getBucketName());
        domainPattern = domainPattern.replace("<region>", s3OssPropertiesUtils.getRegion());
        return StringUtils.trimTrailingCharacter(domainPattern, '/') + '/' + path;
    }

    public PutObjectResult putObject(String bucketName, String objectName, InputStream stream, String contentType) throws IOException {
        return putObject(bucketName, objectName, stream, stream.available(), contentType);
    }

    public PutObjectResult putObject(String bucketName, String objectName, InputStream stream, long size, String contextType) throws IOException {
        AmazonS3 amazonS3 = s3OssPropertiesUtils.getAmazonS3();
        if (amazonS3 == null) {
            throw new BizException("S3 OSS客户端创建失败");
        }
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(size);
        objectMetadata.setContentType(contextType);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, stream, objectMetadata);
        putObjectRequest.getRequestClientOptions().setReadLimit(Long.valueOf(size).intValue() + 1);
        return amazonS3.putObject(putObjectRequest);
    }

}
