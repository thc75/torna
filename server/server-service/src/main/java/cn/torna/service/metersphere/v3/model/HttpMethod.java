package cn.torna.service.metersphere.v3.model;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * http请求方式.
 */
public enum HttpMethod {

    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    OPTIONS,
    TRACE,
    NONE;

    public static HttpMethod of(String method) {
        try {
            return HttpMethod.valueOf(method.toUpperCase());
        } catch (Exception e) {
            return NONE;
        }
    }

    /**
     * 请求方法是否允许消息体
     */
    public boolean isAllowBody() {
        Set<HttpMethod> sets = Sets.newHashSet(POST, PUT, DELETE, PATCH);
        return !sets.contains(this);
    }
}
