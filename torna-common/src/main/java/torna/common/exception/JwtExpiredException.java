package torna.common.exception;

/**
 * @author tanghc
 */
public class JwtExpiredException extends RuntimeException implements ExceptionCode {
    @Override
    public ErrorCode getCode() {
        return ErrorCode.JWT_EXPIRED;
    }
}
