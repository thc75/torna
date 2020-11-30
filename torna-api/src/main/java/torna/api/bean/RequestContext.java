package torna.api.bean;

import torna.common.bean.User;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tanghc
 */
public class RequestContext extends ConcurrentHashMap<String, Object> {

    protected static Class<? extends RequestContext> contextClass = RequestContext.class;

    protected static final ThreadLocal<? extends RequestContext> THREAD_LOCAL = ThreadLocal.withInitial(RequestContext::new);

    private static final String MODULE_ID_KEY = "api-module-id";
    private static final String API_USER_KEY = "api-user-obj";

    /**
     * Get the current RequestContext
     *
     * @return the current RequestContext
     */
    public static RequestContext getCurrentContext() {
        return THREAD_LOCAL.get();
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
