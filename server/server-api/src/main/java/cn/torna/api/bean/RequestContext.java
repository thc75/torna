package cn.torna.api.bean;

import cn.torna.common.bean.User;
import cn.torna.dao.entity.Module;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tanghc
 */
public class RequestContext extends ConcurrentHashMap<String, Object> {

    protected static final ThreadLocal<? extends RequestContext> THREAD_LOCAL = ThreadLocal.withInitial(RequestContext::new);

    private static final String MODULE_KEY = "api-module-obj";
    private static final String API_USER_KEY = "api-user-obj";
    private static final String TOKEN_KEY = "api-token";
    private static final String IP_KEY = "api-client-ip";

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

    public Module getModule() {
        return (Module) this.get(MODULE_KEY);
    }

    public long getModuleId() {
        return getModule().getId();
    }

    public void setModule(Module module) {
        this.put(MODULE_KEY, module);
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

    public void setIp(String ip) {
        put(IP_KEY, ip);
    }

    public String getIp() {
        return (String) get(IP_KEY);
    }
}
