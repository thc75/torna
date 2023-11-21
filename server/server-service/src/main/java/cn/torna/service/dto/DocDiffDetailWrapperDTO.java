package cn.torna.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author thc
 */
@AllArgsConstructor
@Data
public class DocDiffDetailWrapperDTO {

    /**
     * 变更位置，0：文档名称，1：文档描述，2：contentType，3：httpMethod，10：参数名称，11：参数类型，12：参数必填，13：参数最大长度，14：参数描述，15：参数示例值
     */
    private Byte positionType;

    private List<DocDiffDetailDTO> docDiffDetails;

}
