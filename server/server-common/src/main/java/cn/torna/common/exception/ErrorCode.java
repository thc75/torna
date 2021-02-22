package cn.torna.common.exception;

/**
 * @author tanghc
 */
public enum ErrorCode {
    // 1000: 登录失败
    LOGIN_FAIL("1000", "login error"),
    JWT_CREATE("1000", "create token error"),
    JWT_ERROR("1000", "invalid token"),
    JWT_EXPIRED("1000", "token expired"),
    SET_PASSWORD("2000", "set password"),
    ;

    ErrorCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final String code;
    private final String msg;

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
