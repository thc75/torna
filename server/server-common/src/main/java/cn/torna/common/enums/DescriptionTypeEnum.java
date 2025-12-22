package cn.torna.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DescriptionTypeEnum {
    HTML("html"),
    MARKDOWN("markdown");

    private final String value;
}
