package cn.torna.service.dto;

/**
 * @author tanghc
 */
public class DocTreeDTO extends TreeDTO {

    public DocTreeDTO(String id, String label, String parentId, byte type) {
        super(id, label, parentId, type);
    }

    @Override
    public Long getDocId() {
        return super.getDocId();
    }
}
