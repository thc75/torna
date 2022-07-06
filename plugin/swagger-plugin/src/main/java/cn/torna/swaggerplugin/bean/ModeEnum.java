package cn.torna.swaggerplugin.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author thc
 */
@AllArgsConstructor
@Getter
public enum ModeEnum {
    MVC(0),
    DUBBO(1)
    ;
    private final int value;
}
