package cn.torna.service.dto;

import lombok.Data;
import cn.torna.common.message.MessageEnum;

/**
 * @author tanghc
 */
@Data
public class MessageDTO {
    private MessageEnum messageEnum;
    private Byte type;
    private Long sourceId;
}
