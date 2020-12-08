package torna.common.exception;

/**
 * @author tanghc
 */
public class JwtErrorException extends RuntimeException implements ExceptionCode {

    @Override
    public ErrorCode getCode() {
        return ErrorCode.JWT_ERROR;
    }
}
