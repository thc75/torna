package cn.torna.manager.file;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author thc
 */
public class QiNiuClient {

    private final UploadManager uploadManager;

    private final Auth auth;

    private final String bucketName;

    private final String domain;
    private final String module;

    private TokenInfo tokenInfo;

    public QiNiuClient(UploadManager uploadManager, Auth auth, String bucketName, String domain, String module) {
        this.uploadManager = uploadManager;
        this.auth = auth;
        this.bucketName = bucketName;
        this.domain = domain;
        this.module = module;
    }

    /**
     * 上传文件
     * @param file 文件
     * @param path 路径，xx/xx/xx.png
     * @return 返回完整的url
     * @throws QiniuException
     */
    public String put(InputStream file, String path) throws IOException {
        path = StringUtils.trimLeadingCharacter(path, '/');
        if (StringUtils.hasText(module)) {
            path = module + '/' + path;
        }
        uploadManager.put(IOUtils.toByteArray(file), path, getToken());
        return StringUtils.trimTrailingCharacter(domain, '/') + "/" + path;
    }

    public String getToken() {
        if (tokenInfo == null || tokenInfo.isExpired()) {
            String token = auth.uploadToken(bucketName);
            // 持续时间，秒
            long durationSeconds = 3000;
            tokenInfo = new TokenInfo(token, System.currentTimeMillis() + (durationSeconds * 1000));
        }
        return tokenInfo.token;
    }

    @AllArgsConstructor
    private static class TokenInfo {
        private String token;
        private long expireAt;

        public boolean isExpired() {
            return System.currentTimeMillis() > expireAt;
        }
    }

}
