package cn.torna.swaggerplugin.builder;

import io.swagger.annotations.ApiOperation;

import java.lang.reflect.Method;

/**
 * Copyright © 2021 DHF Info. Tech Ltd. All rights reserved.
 * <p></p>
 *
 * @author tanghc
 * @version 1.0.0
 * @description
 * @date 2021/7/14/014
 */
public interface RequestInfoBuilder {
    String buildUrl();

    String getHttpMethod();

    String buildContentType();

    Method getMethod();

    ApiOperation getApiOperation();
}
