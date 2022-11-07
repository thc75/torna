package cn.torna.common.util;

import java.time.LocalDateTime;

/**
 * @author tanghc
 */
public class AppKeyUtil {
    public static String createAppKey() {
        LocalDateTime now = LocalDateTime.now();
        return String.valueOf(now.getYear()) + now.getMonth() + now.getDayOfMonth() + IdGen.nextId();
    }

}
