package cn.torna.common.bean;

import lombok.Data;

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

    private Text text;
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
}
