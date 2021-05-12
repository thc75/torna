package cn.torna.common.context;

import java.util.Arrays;

/**
 * @author tanghc
 */
public interface TornaConstants {

    /**
     * 登录成功后跳转页面
     */
    String SUCCESS_HTML = String.join("\n", Arrays.asList(
            "<html><head><script>",
            "localStorage.setItem('torna.token', '%s');",
            "location.href = '/';",
            "</script></head><body></body></html>"
    ));
}
