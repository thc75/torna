package cn.torna.swaggerplugin.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tanghc
 */
@Data
public class ControllerInfo {
    private String name;
    private String description;
    private int position;
    private Class<?> controllerClass;

    private Map<String, Class<?>> genericParamMap = new HashMap<>();

}
