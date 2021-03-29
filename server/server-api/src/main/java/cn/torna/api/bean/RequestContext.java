package cn.torna.api.bean;

import cn.torna.common.bean.User;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tanghc
 */
public class RequestContext extends ConcurrentHashMap<String, Object> {

    protected static final ThreadLocal<? extends RequestContext> THREAD_LOCAL = ThreadLocal.withInitial(() -> new RequestContext());

    private static final String MODULE_ID_KEY = "api-module-id";
    private static final String API_USER_KEY = "api-user-obj";
    private static final String TOKEN_KEY = "api-token";

    /**
     * Get the current RequestContext
     *
     * @return the current RequestContext
     */
    public static RequestContext getCurrentContext() {
        return THREAD_LOCAL.get();
    }

    public void setToken(String token) {
        put(TOKEN_KEY, token);
    }

    public String getToken() {
        return (String) get(TOKEN_KEY);
    }

    public long getModuleId() {
        return (Long) this.get(MODULE_ID_KEY);
    }

    public void setModuleId(long id) {
        this.put(MODULE_ID_KEY, id);
    }

    public void setApiUser(User apiUser) {
        this.put(API_USER_KEY, apiUser);
    }

    public User getApiUser() {
        return (User) get(API_USER_KEY);
    }

    public void reset() {
        THREAD_LOCAL.remove();
    }
}
