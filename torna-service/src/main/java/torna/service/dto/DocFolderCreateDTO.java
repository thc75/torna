package torna.service.dto;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class DocFolderCreateDTO {
    private Long moduleId;
    private String folderName;
    private Long parentId;

    public DocFolderCreateDTO() {
    }

    public DocFolderCreateDTO(Long moduleId, String folderName, Long parentId) {
        this.moduleId = moduleId;
        this.folderName = folderName;
        this.parentId = parentId;
    }
}
