package torna.common.bean;

/**
 * 登录用户信息
 * @author tanghc
 */
public interface User {
    Long getUserId();

    String getUsername();

    String getRealname();

    boolean isAdmin();
}
