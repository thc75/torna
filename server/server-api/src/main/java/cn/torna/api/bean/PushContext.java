package cn.torna.api.bean;

import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocMeta;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PushContext {

    private List<DocMeta> docMetas;
    private List<DocInfoDTO> contentChangedDocs;

    public void addChangedDoc(DocInfoDTO docInfoDTO) {
        this.contentChangedDocs.add(docInfoDTO);
    }

}
