package cn.torna.manager.file;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * s3文件上传，兼容阿里云
 */
@Component
@Getter
public class S3OssPropertiesUtils implements InitializingBean {

    //读取配置文件的内容
    @Value("${s3.oss.endpoint:${aliyun.oss.endpoint:}}")
    private String endpoint;
    @Value("${s3.oss.access-key:${aliyun.oss.access-key:}}")
    private String accessKey;
    @Value("${s3.oss.access-secret:${aliyun.oss.access-secret:}}")
    private String accessSecret;

    @Value("${s3.oss.bucket-name:${aliyun.oss.bucket-name:}}")
    private String bucketName;

    @Value("${s3.oss.region:}")
    private String region;

    // https://s3.<region>.amazonaws.com/<bucketName>
    @Value("${s3.oss.domain-pattern:${aliyun.oss.url-pattern:https://<bucketName>.<endpoint>}}")
    private String domainPattern;

    @Value("${s3.oss.path-style-access:false}")
    private boolean pathStyleAccess;

    private static volatile boolean inited = false;

    public static boolean isUseS3() {
        return inited;
    }

    private AmazonS3 amazonS3;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(endpoint, accessKey, accessSecret)) {
            return;
        }

        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey,accessSecret);
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        amazonS3 = AmazonS3Client.builder()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .withCredentials(awsCredentialsProvider)
                .disableChunkedEncoding()
                .withPathStyleAccessEnabled(isPathStyleAccess())
                .build();
        inited = amazonS3 != null;
    }
}
