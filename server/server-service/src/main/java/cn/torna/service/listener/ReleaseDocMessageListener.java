package cn.torna.service.listener;

import cn.torna.common.enums.MessageNotifyTypeEnum;
import cn.torna.common.enums.ModifyType;
import cn.torna.common.enums.UserSubscribeTypeEnum;
import cn.torna.common.util.DingTalkOrWeComWebHookUtil;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.Project;
import cn.torna.dao.entity.UserDingtalkInfo;
import cn.torna.dao.entity.UserWeComInfo;
import cn.torna.dao.mapper.ModuleMapper;
import cn.torna.dao.mapper.ProjectMapper;
import cn.torna.service.ProjectReleaseService;
import cn.torna.service.UserDingtalkInfoService;
import cn.torna.service.UserSubscribeService;
import cn.torna.service.UserWeComInfoService;
import cn.torna.service.builder.MessageBuilder;
import cn.torna.service.builder.MessageBuilder.UniversalMessage;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.ProjectReleaseDTO;
import cn.torna.service.event.ReleaseDocMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;


/**
 * 版本关联稿件变动消息提醒监听
 * @author qiuyu
 */
@Slf4j
@Component
public class ReleaseDocMessageListener {

    private static final DateTimeFormatter YMDHMS_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private ProjectReleaseService projectReleaseService;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private UserSubscribeService userSubscribeService;

    @Resource
    private UserDingtalkInfoService userDingtalkInfoService;

    @Resource
    private UserWeComInfoService userWeComInfoService;

    @Resource
    private Executor messageExecutor;

    @EventListener
    public void onApplicationEvent(ReleaseDocMessageEvent event) {
        final DocInfoDTO docInfoDTO = event.getDocInfoDTO();
        final ModifyType modifyType = event.getModifyType();
        messageExecutor.execute(() -> {
            // 获取文档关联的有效版本
            List<ProjectReleaseDTO> projectReleaseDTOS = projectReleaseService.relatedValidReleases(docInfoDTO.getProjectId(), docInfoDTO.getId());
            if (CollectionUtils.isEmpty(projectReleaseDTOS)) {
                return;
            }
            // 获取分组后的版本的关注人
            Map<Long, List<Long>> sourceId2UserIdsMap = userSubscribeService.listUserIdsGroupBySourceId(
                    UserSubscribeTypeEnum.RELEASE,
                    projectReleaseDTOS.stream().map(ProjectReleaseDTO::getId).collect(Collectors.toList())
            );

            // 如果没有人关注 则跳过
            if (!CollectionUtils.isEmpty(sourceId2UserIdsMap)) {
                for (ProjectReleaseDTO projectReleaseDTO : projectReleaseDTOS) {
                    // 获取此版本的关注用户
                    List<Long> userIds = sourceId2UserIdsMap.get(projectReleaseDTO.getId());
                    if (CollectionUtils.isEmpty(userIds)) {
                        continue;
                    }

                    String dingDingWebHookUrl = projectReleaseDTO.getDingdingWebhook();
                    // 未配置钉钉机器人URL 则跳过
                    if (StringUtils.hasText(dingDingWebHookUrl) && isUrl(dingDingWebHookUrl)) {
                        // 获取此版本的关注用户钉钉userid
                        List<String> dingDingUserIds = userDingtalkInfoService.list(UserDingtalkInfo::getUserInfoId, userIds)
                                .stream()
                                .map(UserDingtalkInfo::getUserid)
                                .collect(Collectors.toList());
                        String content = buildMessage(MessageNotifyTypeEnum.DING_TALK_WEB_HOOK, docInfoDTO, projectReleaseDTO,
                                modifyType, dingDingUserIds);
                        if (!StringUtils.hasText(content)) {
                            continue;
                        }
                        DingTalkOrWeComWebHookUtil.pushRobotMessage(MessageNotifyTypeEnum.DING_TALK_WEB_HOOK, dingDingWebHookUrl, content, dingDingUserIds);
                    }

                    String weComWebhookUrl = projectReleaseDTO.getWeComWebhook();
                    if (StringUtils.hasText(weComWebhookUrl)) {
                        // 关注的用户的 企业微信手机号码
                        List<String> weComUserMobiles = userWeComInfoService.list(UserWeComInfo::getUserInfoId, userIds)
                                .stream()
                                .map(UserWeComInfo::getMobile)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

                        String content = buildMessage(MessageNotifyTypeEnum.WECOM_WEBHOOK, docInfoDTO, projectReleaseDTO, modifyType, weComUserMobiles);
                        DingTalkOrWeComWebHookUtil.pushRobotMessage(MessageNotifyTypeEnum.WECOM_WEBHOOK, weComWebhookUrl, content, weComUserMobiles);
                    }
                }
            }
        });
    }


    private boolean isUrl(String url) {
        try {
            new URL(url).toExternalForm();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String buildMessage(MessageNotifyTypeEnum notificationType, DocInfoDTO docInfoDTO,
            ProjectReleaseDTO projectReleaseDTO, ModifyType modifyType, List<String> userIds) {
        Project project = projectMapper.getById(docInfoDTO.getProjectId());
        Module module = moduleMapper.getById(docInfoDTO.getModuleId());

        return MessageBuilder.buildMessage(UniversalMessage.builder()
                .notificationType(notificationType)
                .projectName(project.getName())
                .appName(module.getName())
                .releaseNo(projectReleaseDTO.getReleaseNo())
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
