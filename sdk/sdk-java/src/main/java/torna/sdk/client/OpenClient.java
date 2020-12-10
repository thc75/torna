package torna.sdk.client;

import torna.sdk.common.OpenConfig;
import torna.sdk.common.RequestForm;
import torna.sdk.request.BaseRequest;
import torna.sdk.response.BaseResponse;
import torna.sdk.util.JsonUtil;
import torna.sdk.util.SignUtil;
import torna.sdk.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求客户端
 * @author tanghc
 */
public class OpenClient {
    private static final String ACCEPT_LANGUAGE = "Accept-Language";
    private static final String AUTHORIZATION = "Authorization";
    private static final String PREFIX_BEARER = "Bearer ";

    private String url;
    private String appKey;
    private String secret;

    private OpenRequest openRequest = new OpenRequest();

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
    public <T extends BaseResponse<?>> T execute(BaseRequest<T> request) {
        return this.execute(request, null);
    }

    /**
     * 请求接口
     * @param request 请求对象
     * @param jwt jwt
     * @param <T> 返回对应的Response
     * @return 返回Response
     */
    private <T extends BaseResponse<?>> T execute(BaseRequest<T> request, String jwt) {
        RequestForm requestForm = request.createRequestForm();
        // 表单数据
        Map<String, Object> form = requestForm.getForm();
        form.put(OpenConfig.appKeyName, this.appKey);
        // 生成签名，并加入到form中
        String sign = SignUtil.createSign(form, this.secret);
        form.put(OpenConfig.signName, sign);

        // 构建http请求header
        Map<String, String> header = this.buildHeader(jwt);

        String resp = doExecute(this.url, requestForm, header);

        return this.parseResponse(resp, request);
    }

    protected String doExecute(String url, RequestForm requestForm, Map<String, String> header) {
        return openRequest.request(this.url, requestForm, header);
    }

    protected <T extends BaseResponse<?>> T parseResponse(String resp, BaseRequest<T> request) {
        return JsonUtil.parseObject(resp, request.getResponseClass());
    }

    protected Map<String, String> buildHeader(String jwt) {
        Map<String, String> header = new HashMap<String, String>();
        header.put(ACCEPT_LANGUAGE, OpenConfig.locale);
        if (StringUtil.isNotEmpty(jwt)) {
            header.put(AUTHORIZATION, PREFIX_BEARER + jwt);
        }
        return header;
    }

    public OpenRequest getOpenRequest() {
        return openRequest;
    }

    public void setOpenRequest(OpenRequest openRequest) {
        this.openRequest = openRequest;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

}