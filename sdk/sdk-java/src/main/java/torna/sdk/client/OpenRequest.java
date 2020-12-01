package torna.sdk.client;

import torna.sdk.common.RequestForm;
import torna.sdk.common.RequestMethod;
import torna.sdk.common.UploadFile;
import torna.sdk.response.BaseResponse;
import torna.sdk.util.JsonUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 负责请求操作
 *
 * @author tanghc
 */
public class OpenRequest {
    private static final String AND = "&";
    private static final String EQ = "=";
    private static final String UTF8 = "UTF-8";
    private static final String METHOD_GET = "get";

    private static final String HTTP_ERROR_CODE = "-400";

    private final OpenHttp openHttp = new OpenHttp();

    public String request(String url, RequestForm requestForm, Map<String, String> header) {
        RequestMethod requestMethod = requestForm.getRequestMethod();
        boolean isGet = requestMethod.name().equalsIgnoreCase(METHOD_GET);
        if (isGet) {
            return this.doGet(url, requestForm, header);
        } else {
            return this.doPost(url, requestForm, header);
        }
    }

    protected String doGet(String url, RequestForm requestForm, Map<String, String> header) {
        StringBuilder queryString = new StringBuilder();
        Map<String, Object> form = requestForm.getForm();
        Set<String> keys = form.keySet();
        for (String keyName : keys) {
            try {
                queryString.append(AND).append(keyName).append(EQ)
                        .append(URLEncoder.encode(String.valueOf(form.get(keyName)), UTF8));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        String requestUrl = url + "?" + queryString.toString().substring(1);

        try {
            return openHttp.get(requestUrl, header);
        } catch (IOException e) {
            return this.causeException(e);
        }

    }

    protected String doPost(String url, RequestForm requestForm, Map<String, String> header) {
        try {
            Map<String, Object> form = requestForm.getForm();
            List<UploadFile> files = requestForm.getFiles();
            if (files != null && files.size() > 0) {
                return openHttp.postFile(url, form, header, files);
            } else {
                return openHttp.postJsonBody(url, JsonUtil.toJSONString(form), header);
            }
        } catch (IOException e) {
            return this.causeException(e);
        }
    }

    public String postJsonBody(String url, String json) throws IOException {
        return this.openHttp.postJsonBody(url, json, null);
    }

    protected String causeException(Exception e) {
        ErrorResponse result = new ErrorResponse();
        result.setCode(HTTP_ERROR_CODE);
        result.setMsg(e.getMessage());
        return JsonUtil.toJSONString(result);
    }

    static class ErrorResponse extends BaseResponse<String> {
    }
}
