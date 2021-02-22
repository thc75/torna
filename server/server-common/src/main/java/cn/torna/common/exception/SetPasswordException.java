package cn.torna.common.exception;

/**
 * @author tanghc
 */
public class SetPasswordException extends RuntimeException implements ExceptionCode {
    @Override
    public ErrorCode getCode() {
        return ErrorCode.SET_PASSWORD;
    }
}
