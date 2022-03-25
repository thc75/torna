package cn.torna.service.login;

/**
 * @author thc
 */
public class NotNullStringBuilder {

    private final StringBuilder stringBuilder = new StringBuilder();

    public NotNullStringBuilder append(Number o) {
        stringBuilder.append(formatValue(o));
        return this;
    }

    public NotNullStringBuilder append(String o) {
        stringBuilder.append(formatValue(o));
        return this;
    }

    public NotNullStringBuilder append(String o, String defaultValue) {
        if (o == null || o.length() == 0) {
            o = defaultValue;
        }
        stringBuilder.append(formatValue(o));
        return this;
    }

    private static String formatValue(Number o) {
        return o == null ? "0" : String.valueOf(o);
    }

    private static String formatValue(String o) {
        return o == null ? "" : o;
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
