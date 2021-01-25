package cn.torna.common.message;

import lombok.Builder;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
@Builder
public class Message {
    private String content;
}
