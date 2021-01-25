package cn.torna.manager.doc;

public class ApiInfo {
    private String requestUrl;
    private String path;
    private String method;

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "ApiInfo{" +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
}