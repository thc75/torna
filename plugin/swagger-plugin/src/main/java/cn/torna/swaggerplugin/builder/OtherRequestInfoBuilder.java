package cn.torna.swaggerplugin.builder;

import cn.torna.swaggerplugin.bean.TornaConfig;

import java.lang.reflect.Method;

/**
 * @author tanghc
 * @version 1.0.0
 * @description
 * @date 2021/7/14/014
 */
public class OtherRequestInfoBuilder extends HttpMethodInfoBuilder  {

    public OtherRequestInfoBuilder(Method method, TornaConfig tornaConfig) {
        super(method, tornaConfig);
    }

    @Override
    public String buildUrl() {
        return getMethod().toString();
    }

    @Override
    public String buildContentType() {
        return getTornaConfig().getGlobalContentType();
    }
}
