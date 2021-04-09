package cn.torna.service.dto;

import cn.torna.common.enums.UserSubscribeTypeEnum;
import cn.torna.common.message.MessageEnum;
import lombok.Data;

import java.util.Locale;

/**
 * @author tanghc
 */
@Data
public class MessageDTO {
    private MessageEnum messageEnum;
    private UserSubscribeTypeEnum type;
    private Long sourceId;
    private Locale locale;
}
