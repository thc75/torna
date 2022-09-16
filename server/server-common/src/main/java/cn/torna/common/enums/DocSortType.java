package cn.torna.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author thc
 */
@AllArgsConstructor
@Getter
public enum DocSortType {
    BY_ORDER("by_order"),
    BY_NAME("by_name"),
    BY_URL("by_url"),

    ;
    private final String type;

    public static DocSortType of(String type) {
        if (type == null) {
            return BY_ORDER;
        }
        for (DocSortType value : DocSortType.values()) {
            if (Objects.equals(type, value.getType())) {
                return value;
            }
        }
        return BY_ORDER;
    }
}
