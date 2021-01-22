package torna.service;

import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import com.gitee.fastmybatis.core.query.param.SchPageableParam;
import com.gitee.fastmybatis.core.support.PageEasyui;
import com.gitee.fastmybatis.core.util.MapperUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import torna.common.bean.Booleans;
import torna.common.enums.UserSubscribeTypeEnum;
import torna.common.message.Message;
import torna.common.message.MessageEnum;
import torna.common.message.MessageFactory;
import torna.common.support.BaseService;
import torna.common.util.IdUtil;
import torna.common.util.ThreadPoolUtil;
import torna.dao.entity.DocInfo;
import torna.dao.entity.UserMessage;
import torna.dao.mapper.UserMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import torna.service.dto.MessageDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class UserMessageService extends BaseService<UserMessage, UserMessageMapper> {

    @Autowired
    private UserSubscribeService userSubscribeService;

    public List<UserMessage> listUserUnReadMessage(long userId, int limit) {
        Query query = new Query()
                .eq("user_id", userId)
                .eq("is_read", Booleans.FALSE)
                .orderby("id", Sort.DESC)
                .limit(0, limit);
        return this.list(query);
    }

    public PageEasyui<UserMessage> pageMessage(long userId, SchPageableParam pageableParam) {
        Query query = new Query()
                .eq("user_id", userId)
                .limit(pageableParam.getStart(), pageableParam.getLimit())
                .orderby("is_read", Sort.ASC)
                .orderby("id", Sort.DESC);

        return this.page(query);
    }

    public void setRead(long id) {
        UserMessage userMessage = this.getById(id);
        userMessage.setIsRead(Booleans.TRUE);
        this.update(userMessage);
    }

    /**
     * 消息全部已读
     * @param userId
     */
    public void setReadAll(long userId) {
        this.getMapper().readAll(userId);
    }

    /**
     * 发送站内信
     * @param userIds 用户
     * @param messageDTO messageDTO
     * @param params 消息参数
     */
    public void sendMessage(List<Long> userIds, MessageDTO messageDTO, Object... params) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        ThreadPoolUtil.execute(() -> {
            MessageEnum messageEnum = messageDTO.getMessageEnum();
            Message message = messageEnum.getMessageMeta().getMessage(request.getLocale(), params);
            String content = message.getContent();
            List<UserMessage> userMessages = userIds.stream()
                    .map(userId -> {
                        UserMessage userMessage = new UserMessage();
                        userMessage.setUserId(userId);
                        userMessage.setMessage(content);
                        userMessage.setIsRead(Booleans.FALSE);
                        userMessage.setType(messageDTO.getType());
                        userMessage.setSourceId(messageDTO.getSourceId());
                        return userMessage;
                    })
                    .collect(Collectors.toList());

            this.saveBatch(userMessages);
        });
    }

    public void sendMessageByModifyDoc(DocInfo docInfo) {
        List<Long> finalUserIds = getSubscribeDocUserId(docInfo);
        if (finalUserIds.isEmpty()) {
            return;
        }
        String remark = docInfo.getRemark();
        MessageEnum messageEnum = StringUtils.isEmpty(remark) ? MessageEnum.DOC_UPDATE : MessageEnum.DOC_UPDATE_REMARK;
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageEnum(messageEnum);
        messageDTO.setType(UserSubscribeTypeEnum.DOC.getType());
        messageDTO.setSourceId(docInfo.getId());
        this.sendMessage(finalUserIds, messageDTO, docInfo.getModifierName(), docInfo.getName(), remark);
    }

    public void sendMessageByDeleteDoc(DocInfo docInfo) {
        List<Long> finalUserIds = getSubscribeDocUserId(docInfo);
        if (finalUserIds.isEmpty()) {
            return;
        }
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageEnum(MessageEnum.DOC_DELETE);
        messageDTO.setType(UserSubscribeTypeEnum.DOC.getType());
        messageDTO.setSourceId(docInfo.getId());
        this.sendMessage(finalUserIds, messageDTO, docInfo.getModifierName(), docInfo.getName());
    }

    private List<Long> getSubscribeDocUserId(DocInfo docInfo) {
        List<Long> userIds = userSubscribeService.listUserIds(UserSubscribeTypeEnum.DOC, docInfo.getId());
        return userIds.stream()
                .filter(userId -> !Objects.equals(docInfo.getModifierId(), userId))
                .collect(Collectors.toList());
    }

}