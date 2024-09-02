package cn.torna.common.util;

import cn.torna.common.bean.DingdingWebHookBody;
import cn.torna.common.bean.HttpHelper;
import cn.torna.common.bean.WeComWebHookBody;
import cn.torna.common.enums.MessageNotifyTypeEnum;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author thc
 */
@Slf4j
public class DingTalkOrWeComWebHookUtil {

    public static final String CODE_SUCCESS = "0";

    /**
     * 推送钉钉/企业微信机器人消息
     *
     * @param url     推送完整url
     * @param content 推送内容
     * @param userIds @用户的userId
     */
    public static void pushRobotMessage(MessageNotifyTypeEnum notificationType, String url, String content, List<String> userIds) {
        pushRobotMessage(notificationType, url, content, userIds, null);
    }

    /**
     * 推送钉钉/企业微信机器人消息
     *
     * @param url     推送完整url
     * @param content 推送内容
     * @param userIds @用户的userId
     * @param userMobiles @用户的手机号
     */
    public static void pushRobotMessage(MessageNotifyTypeEnum notificationType, String url, String content, List<String> userIds, List<String> userMobiles) {
        log.info("pushRobotMessage notificationType:{}, url:{}, userIds:{}, userMobiles:{}, content:\n{} ",
                notificationType.getDescription(), url, userIds, userMobiles, content);
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(content)) {
            return;
        }
        // 移除双重换行
        content = content.replace("\r\n", "\n");

        // 推送钉钉机器人
        if (MessageNotifyTypeEnum.DING_TALK_WEB_HOOK.equals(notificationType)) {
            DingdingWebHookBody dingdingWebHookBody = DingdingWebHookBody.create(content);
            if (!CollectionUtils.isEmpty(userIds)) {
                dingdingWebHookBody.setAt(new DingdingWebHookBody.At(userIds, userMobiles));
            }

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

        // 推送企业微信群机器人
        if (MessageNotifyTypeEnum.WECOM_WEBHOOK.equals(notificationType)) {
            WeComWebHookBody weComWebHookBody = WeComWebHookBody.create(content, userIds);
            try {
                // 推送企业微信群机器人
                String result = HttpHelper.postJson(url, JSON.toJSONString(weComWebHookBody))
                        .execute()
                        .asString();
                // 返回码
                String errCode = JSON.parseObject(result).getString("errcode");
                if (!Objects.equals(errCode, CODE_SUCCESS)) {
                    log.error("推送企业微信群机器人消息异常, result={}, url={}", result, url);
                }
            } catch (Exception e) {
                log.error("推送企业微信群机器人失败, url:{}", url, e);
            }
        }
    }

}
