package cn.torna.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

/**
 * Copyright © 2021 DHF Info. Tech Ltd. All rights reserved.
 * <p></p>
 *
 * @author thc
 * @version 1.0.0
 * @description
 * @date 2021/8/21/021
 */
@Data
public class DingdingWebHookBody {

    private At at;

    /**
     * 消息内容。
     */
    private Text text;

    /**
     * 消息类型，此时固定为：text。
     */
    private String msgtype = "text";


    public static DingdingWebHookBody create(String content) {
        DingdingWebHookBody dingdingWebHookBody = new DingdingWebHookBody();
        Text text = new Text();
        text.setContent(content);
        dingdingWebHookBody.setText(text);
        return dingdingWebHookBody;
    }

    @Data
    private static class Text {
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class At {

        public At(List<String> atUserIds) {
            this.atUserIds = atUserIds;
        }

        /**
         * 被@人的用户userid。<br/>
         * <b>注意</b><br>
         * 在content里添加@人的userid。
         */
        private List<String> atUserIds;

        /**
         * 被@的群成员手机号。<br/>
         * <b>注意</b><br>
         *
         */
        private Collection<String> atMobiles;
    }
}
