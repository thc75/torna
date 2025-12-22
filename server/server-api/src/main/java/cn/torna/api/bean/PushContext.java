package cn.torna.api.bean;

import cn.torna.common.bean.DocChangeContext;
import cn.torna.service.dto.DocMeta;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class PushContext {

    private List<DocMeta> docMetas;
    private List<DocChangeContext> contentChangedDocs;
    private String author;
    private Set<Long> docIds;

}
