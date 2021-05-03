package cn.torna.common.util;

import org.springframework.boot.system.ApplicationHome;

import java.io.File;

/**
 * @author tanghc
 */
public class SystemUtil {

    /**
     * 获取程序执行目录，即jar包所在的目录。此方法只在部署后有用，开发模式下，这里返回target路径
     * @return 返回路径
     */
    public static String getBinPath() {
        ApplicationHome applicationHome = new ApplicationHome(SystemUtil.class);
        File file = applicationHome.getSource();
        if (file == null) {
            return System.getProperty("user.dir");
        }
        return file.getParentFile().toString();
    }
}
