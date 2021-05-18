package cn.torna.manager.doc.swagger;

import java.util.Collection;
import java.util.List;

/**
 * @author tanghc
 */
public class DocItem {
    private Integer projectId;
    /**
     * path+method
     */
    private String id;
    private String module;
    private String path;
    private String summary;
    private String description;
    /** http method */
    private String method;

    private String requestUrl;

    private Collection<String> consumes;
    private Collection<String> produces;

    List<DocParameter> queryParameters;
    List<DocParameter> requestParameters;
    List<DocParameter> responseParameters;

    /** 是否是上传文件请求 */
    private boolean uploadRequest;
    /** 是否多文件上传 */
    private boolean multiple;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Collection<String> getConsumes() {
        return consumes;
    }

    public void setConsumes(Collection<String> consumes) {
        this.consumes = consumes;
    }

    public Collection<String> getProduces() {
        return produces;
    }

    public void setProduces(Collection<String> produces) {
        this.produces = produces;
    }

    public List<DocParameter> getRequestParameters() {
        return requestParameters;
    }

    public void setRequestParameters(List<DocParameter> requestParameters) {
        this.requestParameters = requestParameters;
    }

    public List<DocParameter> getResponseParameters() {
        return responseParameters;
    }

    public void setResponseParameters(List<DocParameter> responseParameters) {
        this.responseParameters = responseParameters;
    }

    public boolean getUploadRequest() {
        return uploadRequest;
    }

    public void setUploadRequest(boolean uploadRequest) {
        this.uploadRequest = uploadRequest;
    }

    public boolean getMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public List<DocParameter> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(List<DocParameter> queryParameters) {
        this.queryParameters = queryParameters;
    }

    @Override
    public String toString() {
        return "DocItem{" +
                "projectId=" + projectId +
                ", id='" + id + '\'' +
                ", module='" + module + '\'' +
                ", path='" + path + '\'' +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", method='" + method + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                ", consumes=" + consumes +
                ", produces=" + produces +
                ", queryParameters=" + queryParameters +
                ", requestParameters=" + requestParameters +
                ", responseParameters=" + responseParameters +
                ", uploadRequest=" + uploadRequest +
                ", multiple=" + multiple +
                '}';
    }
}
