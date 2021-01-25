package cn.torna.common.exception;

/**
 * @author tanghc
 */
public class BizException extends RuntimeException {
    public BizException(String message) {
        super(message);
    }
}
