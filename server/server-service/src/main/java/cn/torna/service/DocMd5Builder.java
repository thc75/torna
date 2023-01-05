package cn.torna.service;

import cn.torna.service.dto.DocInfoDTO;

/**
 * @author thc
 */
public interface DocMd5Builder {

    String buildMd5(DocInfoDTO docInfoDTO);

}
