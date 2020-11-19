package torna.service.dto;

import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class DocFolderCreateDTO {
    private Long projectId;
    private String folderName;
    private Long parentId;

    public DocFolderCreateDTO() {
    }

    public DocFolderCreateDTO(Long projectId, String folderName, Long parentId) {
        this.projectId = projectId;
        this.folderName = folderName;
        this.parentId = parentId;
    }
}
