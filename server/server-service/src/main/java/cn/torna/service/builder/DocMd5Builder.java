package cn.torna.service.builder;

import cn.torna.service.dto.DocInfoDTO;

/**
 * @author thc
 */
public interface DocMd5Builder {

    /**
     * 生成文档md5
     *
     * @param docInfoDTO
     * @return
     */
    String buildMd5(DocInfoDTO docInfoDTO);

}
