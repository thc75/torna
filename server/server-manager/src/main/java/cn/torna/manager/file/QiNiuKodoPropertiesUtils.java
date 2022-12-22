package cn.torna.manager.file;

import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class QiNiuKodoPropertiesUtils implements InitializingBean {

    @Value("${qiniu.kodo.access-key:}")
    private String accessKey;
    @Value("${qiniu.kodo.secret-key:}")
    private String secretKey;
    @Value("${qiniu.kodo.bucket-name:}")
    private String bucketName;
    @Value("${qiniu.kodo.domain:}")
    private String domain;

    @Value("${qiniu.kodo.module:torna}")
    private String module;

    @Value("${qiniu.kodo.region:autoRegion}")
    private String region;

    @Value("${qiniu.kodo.use-https-domains:}")
    private String useHttpsDomains;

    private static QiNiuClient qiNiuClient;

    public static QiNiuClient getQiNiuClient() {
        return qiNiuClient;
    }

    public static boolean isUseQiNiuKodo() {
        return qiNiuClient != null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(bucketName, accessKey, secretKey)) {
            return;
        }
        Configuration cfg = new Configuration(buildRegin());
        if (StringUtils.hasText(useHttpsDomains)) {
            cfg.useHttpsDomains = Boolean.parseBoolean(useHttpsDomains);
        } else if (domain.startsWith("http://")) {
            cfg.useHttpsDomains = false;
        }
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        qiNiuClient = new QiNiuClient(uploadManager, auth, bucketName, domain, module);
    }

    private Region buildRegin() {
        switch (region) {
            case "huadong":
                return Region.huadong();
            case "huabei":
                return Region.huabei();
            case "huanan":
                return Region.huanan();
            case "beimei":
                return Region.beimei();
            case "xinjiapo":
                return Region.xinjiapo();
            default: {
                return Region.autoRegion();
            }
        }
    }
}
