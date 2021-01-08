package torna.common.bean;

/**
 * @author tanghc
 */
public interface Booleans {
    byte TRUE = 1;
    byte FALSE = 0;

    static boolean isTrue(Byte b) {
        return b != null && b == TRUE;
    }
}
