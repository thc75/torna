package cn.torna.service.dto;

import lombok.Data;

/**
 * @author thc
 */
@Data
public class DocDiffDTO {

    private String md5Old;

    private DocInfoDTO docInfoDTO;

    public DocDiffDTO(String md5Old, DocInfoDTO docInfoDTO) {
        this.md5Old = md5Old;
        this.docInfoDTO = docInfoDTO;
    }
}
