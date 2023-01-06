package cn.torna.service.builder;

import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.login.NotNullStringBuilder;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author thc
 */
public class ProDocMd5Builder extends DefaultDocMd5Builder {

    @Override
    public String getDocMd5(DocInfoDTO docInfoDTO) {
        NotNullStringBuilder content = new NotNullStringBuilder()
                .append(docInfoDTO.getName())
                .append(docInfoDTO.getHttpMethod())
                .append(docInfoDTO.getUrl())
                .append(docInfoDTO.getContentType())
                .append(docInfoDTO.getDescription())
                .append(docInfoDTO.getDeprecated())
                .append(docInfoDTO.getIsUseGlobalHeaders())
                .append(docInfoDTO.getIsUseGlobalParams())
                .append(docInfoDTO.getIsUseGlobalReturns())
                .append(docInfoDTO.getIsRequestArray())
                .append(docInfoDTO.getIsResponseArray())
                .append(docInfoDTO.getRequestArrayType())
                .append(docInfoDTO.getResponseArrayType())
                .append(docInfoDTO.getStatus())
                .append(docInfoDTO.getAuthor())
                .append(docInfoDTO.getIsShow())
                .append(getDocParamsMd5(docInfoDTO));
        return DigestUtils.md5Hex(content.toString());
    }
}
