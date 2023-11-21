package cn.torna.service.dto;

import cn.torna.common.bean.User;
import lombok.Data;

/**
 * @author thc
 */
@Data
public class DocCommentDTO {

    private Long docId;

    private String content;

    /**
     * 回复id，即：parent_id
     */
    private Long replyId;

    private User user;

}
