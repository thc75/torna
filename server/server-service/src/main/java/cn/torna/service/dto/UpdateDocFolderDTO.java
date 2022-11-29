package cn.torna.service.dto;

import cn.torna.common.bean.User;
import lombok.Builder;
import lombok.Data;

/**
 * @author thc
 */
@Data
public class UpdateDocFolderDTO {

    private Long id;
    private String name;
    private Long parentId;
    private User user;

}
