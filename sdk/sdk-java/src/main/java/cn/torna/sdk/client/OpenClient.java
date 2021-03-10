package cn.torna.sdk.client;

import cn.torna.sdk.common.OpenConfig;
import cn.torna.sdk.common.RequestForm;
import cn.torna.sdk.request.BaseRequest;
import cn.torna.sdk.response.BaseResponse;
import cn.torna.sdk.util.SignUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求客户端
 * @author tanghc
 */
public class OpenClient {
    private static final String ACCEPT_LANGUAGE = "Accept-Language";

    private final String url;
    private final String appKey;
    private final String secret;

    private OpenRequest openRequest = new OpenRequest();

    /**
     * 创建客户端
     * @param url 请求URL
     * @param appKey appKey
     * @param secret secret
     */
    public OpenClient(String url, String appKey, String secret) {
        this.url = url;
        this.appKey = appKey;
        this.secret = secret;
    }

    /**
     * 请求接口
     * @param request 请求对象
     * @param <T> 返回对应的Response
     * @return 返回Response
     */
    public  <T extends BaseResponse<?>> T execute(BaseRequest<T> request) {
        RequestForm requestForm = request.createRequestForm();
        // 表单数据
        Map<String, Object> form = requestForm.getForm();
        form.put(OpenConfig.appKeyName, this.appKey);
        // 生成签名，并加入到form中
        String sign = SignUtil.createSign(form, this.secret);
        form.put(OpenConfig.signName, sign);

        // 构建http请求header
        Map<String, String> header = this.buildHeader();

        String resp = doExecute(this.url, requestForm, header);

        return request.parseResponse(resp);
    }

    protected String doExecute(String url, RequestForm requestForm, Map<String, String> header) {
        return openRequest.request(url, requestForm, header);
    }

    protected Map<String, String> buildHeader() {
        Map<String, String> header = new HashMap<>();
        header.put(ACCEPT_LANGUAGE, OpenConfig.locale);
        return header;
    }

    public void setOpenRequest(OpenRequest openRequest) {
        this.openRequest = openRequest;
    }

}
