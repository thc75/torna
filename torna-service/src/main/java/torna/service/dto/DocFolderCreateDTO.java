package torna.service.dto;

import lombok.Data;
import torna.common.bean.User;

/**
 * @author tanghc
 */
@Data
public class DocFolderCreateDTO {
    private Long moduleId;
    private String name;
    private Long parentId;
    private User user;

}
