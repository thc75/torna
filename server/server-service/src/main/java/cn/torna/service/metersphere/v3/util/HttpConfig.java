package cn.torna.service.metersphere.v3.util;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

public class HttpConfig {
    private static final String HTTPS = "https";

    public static CloseableHttpClient getOneHttpClient(String url) {
        try {
            if (url.startsWith(HTTPS)) {
                // https 增加信任设置
                TrustStrategy trustStrategy = new TrustSelfSignedStrategy();
                SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(trustStrategy).build();
                HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
                return HttpClients.custom().setSSLContext(sslContext).setSSLHostnameVerifier(hostnameVerifier).build();
            } else {
                // http
                return HttpClientBuilder.create().build();
            }
        } catch (Exception e) {
            throw new RuntimeException("HttpClient构建失败", e);
        }
    }
}
