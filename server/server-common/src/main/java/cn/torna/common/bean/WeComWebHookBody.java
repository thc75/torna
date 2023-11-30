package cn.torna.common.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * Copyright © 2021 DHF Info. Tech Ltd. All rights reserved.
 * <p></p>
 *
 * @author lin
 * @version 1.0.0
 * @description 企业微信群机器人 Webhook消息体
 * @date 2023/11/29
 */
@Data
public class WeComWebHookBody {
    /**
     * 消息内容。
     */
    private Text text;

    /**
     * 消息类型，此时固定为：text。
     */
    @JSONField(name = "msgtype")
    private String msgType = "text";


    public static WeComWebHookBody create(String content, List<String> mentionedMobileList) {
        WeComWebHookBody weComWebHookBody = new WeComWebHookBody();
        Text text = new Text();
        text.setContent(content);
        text.setMentionedMobileList(mentionedMobileList);
        weComWebHookBody.setText(text);
        return weComWebHookBody;
    }


    @Data
    private static class Text {

        /**
         * 文本内容，最长不超过2048个字节，必须是utf8编码
         */
        private String content;

        /**
         * userid的列表，提醒群中的指定成员(@某个成员)，@all表示提醒所有人，如果开发者获取不到userid，可以使用mentioned_mobile_list
         */
        @JSONField(name = "mentioned_list")
        private List<String> mentionedList;

        /**
         * 手机号列表，提醒手机号对应的群成员(@某个成员)，@all表示提醒所有人
         */
        @JSONField(name = "mentioned_mobile_list")
        private List<String> mentionedMobileList;
    }
}
