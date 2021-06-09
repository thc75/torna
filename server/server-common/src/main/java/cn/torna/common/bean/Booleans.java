package cn.torna.common.bean;

/**
 * @author tanghc
 */
public interface Booleans {
    byte TRUE = 1;
    byte FALSE = 0;

    /**
     * 是否为true，当val为null，返回false
     * @param val 值
     * @return 返回true/false
     */
    static boolean isTrue(Byte val) {
        return isTrue(val, false);
    }

    /**
     * 是否为true
     * @param val 值
     * @param whenNull 当val为null时，返回whenNull指定的值
     * @return 返回true/false
     */
    static boolean isTrue(Byte val, boolean whenNull) {
        if (val == null) {
            return whenNull;
        }
        return val == TRUE;
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
