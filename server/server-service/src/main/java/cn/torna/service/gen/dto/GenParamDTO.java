package cn.torna.service.gen.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 六如
 */
@Data
public class GenParamDTO {

    @NotNull
    private Long docId;

    @NotNull
    private Long templateId;
}
