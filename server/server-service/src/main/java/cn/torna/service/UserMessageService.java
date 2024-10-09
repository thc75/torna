package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.enums.MesssageTypeEnum;
import cn.torna.common.enums.UserSubscribeTypeEnum;
import cn.torna.common.message.Message;
import cn.torna.common.message.MessageEnum;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.UserInfo;
import cn.torna.dao.entity.UserMessage;
import cn.torna.dao.mapper.UserMessageMapper;
import cn.torna.service.dto.MessageDTO;
import com.gitee.fastmybatis.core.PageInfo;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import com.gitee.fastmybatis.core.query.param.PageParam;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class UserMessageService extends BaseLambdaService<UserMessage, UserMessageMapper> {

    @Autowired
    private UserSubscribeService userSubscribeService;

    @Autowired
    private UserInfoService userInfoService;

    @Value("${torna.message.max-length:250}")
    private int messageMaxLength;

    @Async
    public void sendMessageByModifyDoc(DocInfo docInfo) {
        List<Long> finalUserIds = getSubscribeDocUserId(docInfo);
        if (finalUserIds.isEmpty()) {
            return;
        }
        String remark = docInfo.getRemark();
        MessageEnum messageEnum = StringUtils.isEmpty(remark) ? MessageEnum.DOC_UPDATE : MessageEnum.DOC_UPDATE_REMARK;
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageEnum(messageEnum);
        messageDTO.setType(UserSubscribeTypeEnum.DOC);
        messageDTO.setSourceId(docInfo.getId());
        messageDTO.setLocale(Locale.SIMPLIFIED_CHINESE);
        this.sendMessage(finalUserIds, messageDTO, docInfo.getModifierName(), docInfo.getName(), remark);
    }

    @Async
    public void sendMessageByDeleteDoc(DocInfo docInfo) {
        List<Long> finalUserIds = getSubscribeDocUserId(docInfo);
        if (finalUserIds.isEmpty()) {
            return;
        }
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageEnum(MessageEnum.DOC_DELETE);
        messageDTO.setType(UserSubscribeTypeEnum.DOC);
        messageDTO.setSourceId(docInfo.getId());
        messageDTO.setLocale(Locale.SIMPLIFIED_CHINESE);
        this.sendMessage(finalUserIds, messageDTO, docInfo.getModifierName(), docInfo.getName());
    }

    /**
     * 给超级管理员发站内信
     *
     * @param content 信息内容
     */
    @Async
    public void sendMessageToAdmin(MessageDTO messageDTO, String content) {
        List<Long> userIds = userInfoService.listSuperAdmin()
                .stream()
                .map(UserInfo::getId)
                .collect(Collectors.toList());
        this.sendMessage(userIds, messageDTO, content);
    }

    /**
     * 给超级管理员发站内信
     *
     * @param content 信息内容
     */
    @Async
    public void sendMessageToUser(MessageDTO messageDTO, String content, Long userId) {
        this.sendMessage(Collections.singletonList(userId), messageDTO, content);
    }

    public List<UserMessage> listUserUnReadMessage(long userId, int limit) {
        Query query = this.query()
                .eq(UserMessage::getUserId, userId)
                .eq(UserMessage::getIsRead, Booleans.FALSE)
                .orderBy(UserMessage::getId, Sort.DESC);
        if (limit > 0) {
            query.limit(0, limit);
        }
        return this.list(query);
    }


    public PageInfo<UserMessage> pageMessage(long userId, PageParam pageableParam) {
        Query query = pageableParam.toQuery().toLambdaQuery(UserMessage.class)
                .eq(UserMessage::getUserId, userId)
                .orderBy(UserMessage::getIsRead, Sort.ASC)
                .orderBy(UserMessage::getId, Sort.DESC);

        return this.page(query);
    }

    /**
     * 物理删除所有推送数据
     *
     * @param userId 用户id
     */
    public void deletePushMessage(long userId) {
        this.query()
                .eq(UserMessage::getUserId, userId)
                .eq(UserMessage::getType, MesssageTypeEnum.PUSH_DOC.getType())
                .deleteForce();
    }

    public void setRead(long id) {
        UserMessage userMessage = this.getById(id);
        userMessage.setIsRead(Booleans.TRUE);
        this.update(userMessage);
    }

    /**
     * 消息全部已读
     *
     * @param userId
     */
    public void setReadAll(long userId) {
        this.getMapper().readAll(userId);
    }

    /**
     * 发送站内信
     *
     * @param userIds    用户
     * @param messageDTO messageDTO
     * @param params     消息参数
     */
    private void sendMessage(List<Long> userIds, MessageDTO messageDTO, Object... params) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        MessageEnum messageEnum = messageDTO.getMessageEnum();
        Locale locale = messageDTO.getLocale();
        if (locale == null) {
            locale = Locale.SIMPLIFIED_CHINESE;
        }
        Message message = messageEnum.getMessageMeta().getMessage(locale, params);
        String content = message.getContent();
        if (content != null && content.length() > messageMaxLength) {
            content = content.substring(0, messageMaxLength);
        }
        String finalContent = content;
        List<UserMessage> userMessages = userIds.stream()
                .map(userId -> {
                    UserMessage userMessage = new UserMessage();
                    userMessage.setUserId(userId);
                    userMessage.setMessage(finalContent);
                    userMessage.setIsRead(Booleans.FALSE);
                    userMessage.setType(messageDTO.getType().getType());
                    userMessage.setSourceId(messageDTO.getSourceId());
                    return userMessage;
                })
                .collect(Collectors.toList());

        this.saveBatch(userMessages);
    }

    private List<Long> getSubscribeDocUserId(DocInfo docInfo) {
        List<Long> userIds = userSubscribeService.listUserIds(UserSubscribeTypeEnum.DOC, docInfo.getId());
        return userIds.stream()
                .filter(userId -> !Objects.equals(docInfo.getModifierId(), userId))
                .collect(Collectors.toList());
    }

}
