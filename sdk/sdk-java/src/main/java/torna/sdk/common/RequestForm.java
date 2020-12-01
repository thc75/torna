package torna.sdk.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestForm  {

    private RequestMethod requestMethod = RequestMethod.POST;
    /** 请求表单内容 */
    private Map<String, Object> form;
    /** 上传文件 */
    private List<UploadFile> files;

    public RequestForm(Map<? extends String, ?> m) {
        this.form = new HashMap<>(m);
    }

    public List<UploadFile> getFiles() {
        return files;
    }

    public void setFiles(List<UploadFile> files) {
        this.files = files;
    }

    public Map<String, Object> getForm() {
        return form;
    }

    public void setForm(Map<String, Object> form) {
        this.form = form;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }
}
