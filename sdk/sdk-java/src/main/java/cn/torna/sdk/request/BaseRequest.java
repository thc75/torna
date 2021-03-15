package cn.torna.sdk.request;

import cn.torna.sdk.common.OpenConfig;
import cn.torna.sdk.common.RequestForm;
import cn.torna.sdk.common.UploadFile;
import cn.torna.sdk.response.BaseResponse;
import cn.torna.sdk.util.ClassUtil;
import cn.torna.sdk.util.JsonUtil;
import cn.torna.sdk.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

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
    private final String token;

    /** 上传文件 */
    @JSONField(serialize = false)
    private List<UploadFile> files;

    @JSONField(serialize = false)
    private final Class<T> responseClass;

    @JSONField(serialize = false)
    public abstract String name();

    @SuppressWarnings("unchecked")
    public BaseRequest(String token) {
        this.responseClass = (Class<T>) ClassUtil.getSuperClassGenricType(this.getClass(), 0);
        this.token = token;
    }

    @JSONField(serialize = false)
    protected String version() {
        return OpenConfig.defaultVersion;
    }

    public RequestForm createRequestForm() {
        String data = JSON.toJSONString(this);
        // 公共参数
        Map<String, Object> param = new HashMap<>();
        String name = name();
        if (name == null) {
            throw new IllegalArgumentException("name不能为null");
        }
        param.put(OpenConfig.apiName, name);
        param.put(OpenConfig.dataName, StringUtil.encodeUrl(data));
        param.put(OpenConfig.timestampName, new SimpleDateFormat(OpenConfig.timestampPattern).format(new Date()));
        param.put(OpenConfig.versionName, version());
        param.put(OpenConfig.accessTokenName, token);
        RequestForm requestForm = new RequestForm(param);
        requestForm.setFiles(this.files);
        return requestForm;
    }

    /**
     * 解析返回结果
     * @param resp 返回结果
     * @return 返回解析后的对象
     */
    public T parseResponse(String resp) {
        return JsonUtil.parseObject(resp, getResponseClass());
    }

    private void setFiles(List<UploadFile> files) {
        this.files = files;
    }

    private Class<T> getResponseClass() {
        return responseClass;
    }
}
