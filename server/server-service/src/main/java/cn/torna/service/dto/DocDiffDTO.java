package cn.torna.service.dto;

import cn.torna.common.bean.User;
import cn.torna.common.enums.ModifySourceEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author thc
 */
@Data
@AllArgsConstructor
public class DocDiffDTO {

    private String md5Old;
    private String md5New;
    private LocalDateTime modifyTime;

    private User user;

    private ModifySourceEnum modifySourceEnum;

}
