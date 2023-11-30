package cn.torna.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * webhook 类型
 *
 * @author Lin
 * @version 1.0.0
 * @since 2023-11-29  16:01:23
 */
@Getter
@RequiredArgsConstructor
public enum MessageNotifyTypeEnum {

    /**
     * 企业微信webhook
     */
    WECOM_WEBHOOK((byte) 1, "企业微信webhook"),

    /**
     *钉钉webhook
     */
    DING_TALK_WEB_HOOK((byte) 2, "钉钉webhook");



    /**
     * 类型code
     */
    private final byte type;
    /**
     * 描述信息
     */
    private final String description;
}
