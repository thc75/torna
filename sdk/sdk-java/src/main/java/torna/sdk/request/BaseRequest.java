package torna.sdk.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import torna.sdk.common.OpenConfig;
import torna.sdk.common.RequestForm;
import torna.sdk.common.RequestMethod;
import torna.sdk.common.UploadFile;
import torna.sdk.response.BaseResponse;
import torna.sdk.util.ClassUtil;
import torna.sdk.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 请求对象父类，后续请求对象都要继承这个类
 * @param <T> 对应的Response对象
 */
public abstract class BaseRequest<T extends BaseResponse<?>> {

    @JSONField(serialize = false)
    private String token;

    /** 上传文件 */
    @JSONField(serialize = false)
    private List<UploadFile> files;

    @JSONField(serialize = false)
    private Class<T> responseClass;

    @JSONField(serialize = false)
    public abstract String name();

    @SuppressWarnings("unchecked")
    public BaseRequest() {
        this.responseClass = (Class<T>) ClassUtil.getSuperClassGenricType(this.getClass(), 0);
    }

    @JSONField(serialize = false)
    protected String version() {
        return OpenConfig.defaultVersion;
    }

    public RequestForm createRequestForm() {
        String data = JSON.toJSONString(this);
        // 公共参数
        Map<String, Object> param = new HashMap<String, Object>();
        param.put(OpenConfig.apiName, name());
        param.put(OpenConfig.dataName, StringUtil.encodeUrl(data));
        param.put(OpenConfig.timestampName, new SimpleDateFormat(OpenConfig.timestampPattern).format(new Date()));
        param.put(OpenConfig.versionName, version());
        param.put(OpenConfig.accessTokenName, token);
        RequestForm requestForm = new RequestForm(param);
        requestForm.setRequestMethod(getRequestMethod());
        requestForm.setFiles(this.files);
        return requestForm;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }


    public void setFiles(List<UploadFile> files) {
        this.files = files;
    }

    public Class<T> getResponseClass() {
        return responseClass;
    }
}
