package cn.torna.common.bean;

/**
 * @author tanghc
 */
public interface Booleans {
    byte TRUE = 1;
    byte FALSE = 0;

    static boolean isTrue(Byte b) {
        return b != null && b == TRUE;
    }

    static boolean isTrue(Long b) {
        return b != null && b == TRUE;
    }

    static byte toValue(Boolean b) {
        if (b == null) {
            return FALSE;
        }
        return b ? TRUE : FALSE;
    }
}
