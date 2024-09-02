package cn.torna.service;

import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.enums.MessageNotifyTypeEnum;
import cn.torna.common.enums.ModifyType;
import cn.torna.common.util.DingTalkOrWeComWebHookUtil;
import cn.torna.common.util.IdUtil;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.Project;
import cn.torna.dao.mapper.ModuleMapper;
import cn.torna.dao.mapper.ProjectMapper;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.event.ReleaseDocMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 负责发送消息，站内信、钉钉、企业微信
 *
 * @author thc
 */
@Service
@Slf4j
public class MessageService {
    private static final DateTimeFormatter YMDHMS_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Autowired
    private ModuleConfigService moduleConfigService;

    @Autowired
    private DocInfoService docInfoService;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 发送第三方消息，钉钉、企业微信
     *
     * @param docInfoDTO 文档
     * @param modifyType 修改方式
     */
    public void pushDocModifyMessage(DocInfoDTO docInfoDTO, ModifyType modifyType) {
        try {
            if (modifyType == ModifyType.UPDATE || modifyType == ModifyType.DELETE) {
                // 钉钉机器人 webhook url
                String url = moduleConfigService.getDingDingRobotWebhookUrl(docInfoDTO.getModuleId());
                if (StringUtils.hasText(url)) {
                    List<String> dingDingUserIds = docInfoService.listSubscribeDocDingDingUserIds(docInfoDTO.getId());
                    String content = buildDingDingMessage(MessageNotifyTypeEnum.DING_TALK_WEB_HOOK, docInfoDTO, modifyType, dingDingUserIds);
                    if (!StringUtils.hasText(content)) {
                        return;
                    }
                    DingTalkOrWeComWebHookUtil.pushRobotMessage(MessageNotifyTypeEnum.DING_TALK_WEB_HOOK, url, content, dingDingUserIds);
                }
                // 这里推送关联版本的钉钉机器人webhook
                applicationContext.publishEvent(new ReleaseDocMessageEvent(this, docInfoDTO, modifyType));
                // 企业微信webhook url
                url = moduleConfigService.getWeComWebhookUrl(docInfoDTO.getModuleId());
                if (StringUtils.hasText(url)) {
                    // 关注的用户的 企业微信手机号码
                    List<String> weComUserMobiles = docInfoService.listSubscribeDocWeComUserMobiles(docInfoDTO.getId());
                    // 如果没有人关注 则跳过
                    if (!CollectionUtils.isEmpty(weComUserMobiles)) {
                        String content = buildDingDingMessage(MessageNotifyTypeEnum.WECOM_WEBHOOK, docInfoDTO, modifyType, weComUserMobiles);
                        DingTalkOrWeComWebHookUtil.pushRobotMessage(MessageNotifyTypeEnum.WECOM_WEBHOOK, url, content, weComUserMobiles);
                    }
                }
            }
        } catch (Exception e) {
            log.error("推送钉钉/企业微信消息失败, doc={}", docInfoDTO.getName(), e);
        }
    }


    private String buildDingDingMessage(MessageNotifyTypeEnum notificationType, DocInfoDTO docInfoDTO, ModifyType modifyType, List<String> userIds) {
        Project project = projectMapper.getById(docInfoDTO.getProjectId());
        Module module = moduleMapper.getById(docInfoDTO.getModuleId());
        Map<String, String> replaceMap = new HashMap<>(16);
        replaceMap.put("{projectName}", project.getName());
        replaceMap.put("{appName}", module.getName());
        replaceMap.put("{docName}", docInfoDTO.getName());
        replaceMap.put("{url}", docInfoDTO.getUrl());
        replaceMap.put("{modifier}", docInfoDTO.getModifierName());
        replaceMap.put("{modifyTime}", docInfoDTO.getGmtModified().format(YMDHMS_PATTERN));
        replaceMap.put("{modifyType}", modifyType.getDescription());
        String frontUrl = EnvironmentKeys.TORNA_FRONT_URL.getValue("http://localhost:7700");
        String docViewUrl = frontUrl + "/#/view/" + IdUtil.encode(docInfoDTO.getId());
        replaceMap.put("{docViewUrl}", docViewUrl);
        String content = null;
        // 钉钉
        if (notificationType.equals(MessageNotifyTypeEnum.DING_TALK_WEB_HOOK)) {
            String atUser = "";
            if (!CollectionUtils.isEmpty(userIds)) {
                atUser = userIds.stream()
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
            content = content.replace(entry.getKey(), entry.getValue());
        }
        return content;
    }
}
