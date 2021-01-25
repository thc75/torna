package cn.torna.common.exception;

/**
 * @author tanghc
 */
public class JwtCreateException extends RuntimeException implements ExceptionCode {
    @Override
    public ErrorCode getCode() {
        return ErrorCode.JWT_CREATE;
    }
}
