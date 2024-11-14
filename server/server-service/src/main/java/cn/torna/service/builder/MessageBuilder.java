package cn.torna.service.builder;

import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.enums.MessageNotifyTypeEnum;
import cn.torna.common.enums.ModifyType;
import cn.torna.common.util.IdUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 统一消息构建器
 * @author Jason Kung
 * @date 2024/11/14 10:28
 */
public class MessageBuilder {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String buildMessage(UniversalMessage universalMessage) {
        Map<String, String> replaceMap = new HashMap<>(16);
        replaceMap.put("{projectName}", universalMessage.getProjectName());
        replaceMap.put("{appName}", universalMessage.getAppName());
        replaceMap.put("{releaseNo}", universalMessage.getReleaseNo());
        replaceMap.put("{docName}", universalMessage.getDocName());
        replaceMap.put("{url}", universalMessage.getUrl());
        replaceMap.put("{modifier}", universalMessage.getModifier());
        replaceMap.put("{modifyTime}", universalMessage.getModifyTime().format(DATE_TIME_FORMATTER));
        replaceMap.put("{modifyType}", universalMessage.getModifyType().getDescription());
        String frontUrl = EnvironmentKeys.TORNA_FRONT_URL.getValue("http://localhost:7700");
        String docViewUrl = frontUrl + "/#/view/" + IdUtil.encode(universalMessage.getDocId());
        replaceMap.put("{docViewUrl}", docViewUrl);
        String content = null;
        // 钉钉
        MessageNotifyTypeEnum notificationType = universalMessage.getNotificationType();
        List<String> userIdList = universalMessage.getUserIdList();
        if (notificationType.equals(MessageNotifyTypeEnum.DING_TALK_WEB_HOOK)) {
            String atUser = "";
            if (!CollectionUtils.isEmpty(userIdList)) {
                atUser = userIdList.stream()
                        .filter(StringUtils::hasText)
                        .map(userId -> "@" + userId)
                        .collect(Collectors.joining(" "));
            }
            replaceMap.put("{@user}", atUser);
            content = EnvironmentKeys.PUSH_DINGDING_WEBHOOK_CONTENT.getValue();
        }
        if (notificationType.equals(MessageNotifyTypeEnum.WECOM_WEBHOOK)) {
            content = EnvironmentKeys.PUSH_WECOM_WEBHOOK_CONTENT.getValue();
        }
        if (!StringUtils.hasText(content)) {
            return content;
        }
        for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
            String value = entry.getValue() == null ? "" : entry.getValue();
            content = content.replace(entry.getKey(), value);
        }
        return content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UniversalMessage {

        /**
         * 消息通知类型
         */
        private MessageNotifyTypeEnum notificationType;
        /**
         * 项目名称
         */
        private String projectName;
        /**
         * 应用名称
         */
        private String appName;
        /**
         * 版本号
         */
        private String releaseNo;
        /**
         * 文档id
         */
        private Long docId;
        /**
         * 文档名称
         */
        private String docName;
        /**
         * 链接
         */
        private String url;
        /**
         * 修改人
         */
        private String modifier;
        /**
         * 修改时间
         */
        private LocalDateTime modifyTime;
        /**
         * 修改类型
         */
        private ModifyType modifyType;
        /**
         * 用户ID列表
         */
        private List<String> userIdList;
    }
}
