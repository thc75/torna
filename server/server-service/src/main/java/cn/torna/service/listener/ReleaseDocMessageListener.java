package cn.torna.service.listener;

import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.enums.MessageNotifyTypeEnum;
import cn.torna.common.enums.ModifyType;
import cn.torna.common.enums.UserSubscribeTypeEnum;
import cn.torna.common.util.DingTalkOrWeComWebHookUtil;
import cn.torna.common.util.IdUtil;
import cn.torna.dao.entity.Module;
import cn.torna.dao.entity.Project;
import cn.torna.dao.entity.UserDingtalkInfo;
import cn.torna.dao.entity.UserInfo;
import cn.torna.dao.mapper.ModuleMapper;
import cn.torna.dao.mapper.ProjectMapper;
import cn.torna.service.ProjectReleaseService;
import cn.torna.service.UserDingtalkInfoService;
import cn.torna.service.UserInfoService;
import cn.torna.service.UserSubscribeService;
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
                    String url = projectReleaseDTO.getDingdingWebhook();
                    // 未配置钉钉机器人URL 则跳过
                    if (StringUtils.hasText(url) && isUrl(url)) {
                        // 获取此版本的关注用户
                        List<Long> userIds = sourceId2UserIdsMap.get(projectReleaseDTO.getId());
                        if (CollectionUtils.isEmpty(userIds)) {
                            continue;
                        }
                        // 获取此版本的关注用户钉钉userid
                        List<String> dingDingUserIds = userDingtalkInfoService.list(UserDingtalkInfo::getUserInfoId, userIds)
                                .stream()
                                .map(UserDingtalkInfo::getUserid)
                                .collect(Collectors.toList());
                        // 获取此版本的关注用户钉钉手机号码
                        List<String> dingDingUserMobiles = null;
                        // 关注人未绑定钉钉，则跳过此版本文档消息提醒
                        if (CollectionUtils.isEmpty(dingDingUserIds)) {
                            continue;
                        }
                        String content = buildDingDingMessage(MessageNotifyTypeEnum.DING_TALK_WEB_HOOK, docInfoDTO, projectReleaseDTO,
                                modifyType, dingDingUserIds, dingDingUserMobiles);
                        if (!StringUtils.hasText(content)) {
                            continue;
                        }
                        DingTalkOrWeComWebHookUtil.pushRobotMessage(MessageNotifyTypeEnum.DING_TALK_WEB_HOOK, url, content, dingDingUserIds, dingDingUserMobiles);
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


    private String buildDingDingMessage(MessageNotifyTypeEnum notificationType, DocInfoDTO docInfoDTO, ProjectReleaseDTO projectReleaseDTO,
                                        ModifyType modifyType, Collection<String> userIds, Collection<String> userMobiles) {
        Project project = projectMapper.getById(docInfoDTO.getProjectId());
        Module module = moduleMapper.getById(docInfoDTO.getModuleId());
        Map<String, String> replaceMap = new HashMap<>(16);
        replaceMap.put("{projectName}", project.getName());
        replaceMap.put("{appName}", module.getName());
        replaceMap.put("{releaseNo}", projectReleaseDTO.getReleaseNo());
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
            // 有钉钉手机号添加@手机号
            if (!CollectionUtils.isEmpty(userMobiles)) {
                atUser = atUser + userMobiles.stream()
                        .map(userMobile -> "@" + userMobile)
                        .collect(Collectors.joining(" "));
            }
            replaceMap.put("{@user}", atUser);
            content = EnvironmentKeys.PUSH_DINGDING_WEBHOOK_CONTENT.getValue();
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
