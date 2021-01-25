package cn.torna.common.message;

import java.util.Locale;

/**
 * @author tanghc
 */
public class MessageMeta {

    private final String key;

    public MessageMeta(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public Message getMessage(Locale locale, Object... params) {
        return MessageFactory.getError(this, locale, params);
    }

}
