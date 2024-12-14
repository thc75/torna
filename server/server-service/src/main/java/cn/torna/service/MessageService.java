package cn.torna.service;

import cn.torna.common.enums.MessageNotifyTypeEnum;
import cn.torna.common.enums.ModifyType;
import cn.torna.common.util.DingTalkOrWeComWebHookUtil;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.Project;
import cn.torna.dao.mapper.ModuleMapper;
import cn.torna.dao.mapper.ProjectMapper;
import cn.torna.service.builder.MessageBuilder;
import cn.torna.service.builder.MessageBuilder.UniversalMessage;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.event.ReleaseDocMessageEvent;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 负责发送消息，站内信、钉钉、企业微信
 *
 * @author thc
 */
@Service
@Slf4j
public class MessageService {


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
            if (modifyType == ModifyType.UPDATE || modifyType == ModifyType.DELETE || modifyType == ModifyType.ADD) {
                // 钉钉机器人 webhook url
                String url = moduleConfigService.getDingDingRobotWebhookUrl(docInfoDTO.getModuleId());
                if (StringUtils.hasText(url)) {
                    List<String> dingDingUserIds = docInfoService.listSubscribeDocDingDingUserIds(docInfoDTO.getId());
                    String content = buildMessage(MessageNotifyTypeEnum.DING_TALK_WEB_HOOK, docInfoDTO, modifyType, dingDingUserIds);
                    if (!StringUtils.hasText(content)) {
                        return;
                    }

                    if (modifyType == ModifyType.ADD || !CollectionUtils.isEmpty(dingDingUserIds)) {
                        DingTalkOrWeComWebHookUtil.pushRobotMessage(MessageNotifyTypeEnum.DING_TALK_WEB_HOOK, url, content, dingDingUserIds);
                    }
                }

                if (modifyType != ModifyType.ADD) {
                    // 这里推送关联版本的webhook
                    applicationContext.publishEvent(new ReleaseDocMessageEvent(this, docInfoDTO, modifyType));
                }

                // 企业微信webhook url
                url = moduleConfigService.getWeComWebhookUrl(docInfoDTO.getModuleId());
                if (StringUtils.hasText(url)) {
                    // 关注的用户的 企业微信手机号码
                    List<String> weComUserMobiles = docInfoService.listSubscribeDocWeComUserMobiles(docInfoDTO.getId());
                    // 如果是新增或者有人关注才推送
                    if (modifyType == ModifyType.ADD || !CollectionUtils.isEmpty(weComUserMobiles)) {
                        String content = buildMessage(MessageNotifyTypeEnum.WECOM_WEBHOOK, docInfoDTO, modifyType, weComUserMobiles);
                        DingTalkOrWeComWebHookUtil.pushRobotMessage(MessageNotifyTypeEnum.WECOM_WEBHOOK, url, content, weComUserMobiles);
                    }
                }
            }
        } catch (Exception e) {
            log.error("推送钉钉/企业微信消息失败, doc={}", docInfoDTO.getName(), e);
        }
    }


    private String buildMessage(MessageNotifyTypeEnum notificationType, DocInfoDTO docInfoDTO, ModifyType modifyType, List<String> userIds) {
        Project project = projectMapper.getById(docInfoDTO.getProjectId());
        Module module = moduleMapper.getById(docInfoDTO.getModuleId());

        return MessageBuilder.buildMessage(UniversalMessage.builder()
                        .notificationType(notificationType)
                        .projectName(project.getName())
                        .appName(module.getName())
                        .releaseNo(null)
                        .docId(docInfoDTO.getId())
                        .docName(docInfoDTO.getName())
                        .url(docInfoDTO.getUrl())
                        .modifier(docInfoDTO.getModifierName())
                        .modifyTime(docInfoDTO.getGmtModified())
                        .modifyType(modifyType)
                        .userIdList(userIds)
                .build());
    }
}
