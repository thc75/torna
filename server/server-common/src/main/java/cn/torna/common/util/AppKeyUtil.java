package cn.torna.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author tanghc
 */
public class AppKeyUtil {
    public static String createAppKey() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date()) + IdGen.nextId();
    }

}
