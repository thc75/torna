package cn.torna.common.util;

import cn.torna.common.bean.DingdingWebHookBody;
import cn.torna.common.bean.HttpHelper;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author thc
 */
@Slf4j
public class DingTalkUtil {

    public static final String CODE_SUCCESS = "0";

    /**
     *
     * 推送钉钉机器人消息
     * @param url 推送完整url
     * @param content 推送内容
     * @param userIds @用户的userId
     */
    public static void pushRobotMessage(String url, String content, List<String> userIds) {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(content)) {
            return;
        }
        // 移除双重换行
        content = content.replace("\r\n", "\n");
        DingdingWebHookBody dingdingWebHookBody = DingdingWebHookBody.create(content);
        dingdingWebHookBody.setAt(new DingdingWebHookBody.At(userIds));
        try {
            // 推送钉钉机器人
            String result = HttpHelper.postJson(url, JSON.toJSONString(dingdingWebHookBody))
                    .execute()
                    .asString();
            String errcode = JSON.parseObject(result).getString("errcode");
            if (!Objects.equals(errcode, CODE_SUCCESS)) {
                log.error("推送钉钉消息异常, result={}, url={}", result, url);
            }
        } catch (Exception e) {
            log.error("推送钉钉失败, url:{}", url, e);
        }
    }

}
