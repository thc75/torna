package cn.torna.service.dto;

import cn.torna.common.bean.User;
import cn.torna.common.enums.DocDiffModifySourceEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author thc
 */
@Data
@AllArgsConstructor
public class DocDiffDTO {

    private String md5Old;
    private String md5New;

    private User user;

    private DocDiffModifySourceEnum modifySource;

}
