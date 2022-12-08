package cn.torna.manager.file;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AliyunOssPropertiesUtils implements InitializingBean {
 
    //读取配置文件的内容
    @Value("${aliyun.oss.endpoint:}")
    private String endpoint;
    @Value("${aliyun.oss.access-key:}")
    private String accessKey;
    @Value("${aliyun.oss.access-secret:}")
    private String accessSecret;
    @Value("${aliyun.oss.bucket-name:}")
    private String bucketName;

    private static OSS ossClient;
    private static String BUCKET_NAME;
    private static String END_POINT;

    public static OSS getOssClient() {
        return ossClient;
    }

    public static String getEndPoint() {
        return END_POINT;
    }

    public static String getBucketName() {
        return BUCKET_NAME;
    }

    public static boolean isUseAliyunOss() {
        return ossClient != null;
    }
 
    @Override
    public void afterPropertiesSet() throws Exception {
        BUCKET_NAME = bucketName;
        END_POINT = endpoint;
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(endpoint, accessKey, accessSecret)) {
            return;
        }
        ossClient = new OSSClientBuilder().build(endpoint, accessKey, accessSecret);
    }
}
