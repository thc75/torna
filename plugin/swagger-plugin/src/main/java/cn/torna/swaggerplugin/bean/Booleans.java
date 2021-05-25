package cn.torna.swaggerplugin.bean;

public class Booleans {
    public static final byte TRUE = 1;
    public static final byte FALSE = 0;

    public static boolean isTrue(Byte b) {
        return b != null && b == TRUE;
    }

    public static byte toValue(Boolean b) {
        if (b == null) {
            return FALSE;
        }
        return b ? TRUE : FALSE;
    }
}