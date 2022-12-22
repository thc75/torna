package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author thc
 */
@Data
public class DocDiffRecordDTO {

    /**
     * 旧MD5
     */
    private String md5Old;


    /**
     * 新MD5
     */
    private String md5New;


    /**
     * 修改方式，0：推送，1：表单编辑
     */
    private Byte modifySource;


    /**
     * 修改人id
     */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long modifyUserId;


    /**
     * 修改人
     */
    private String modifyNickname;


    /**
     * 变更类型，0：修改，1：新增，2：删除
     */
    private Byte modifyType;


    private LocalDateTime gmtCreate;


    private List<DocDiffDetailDTO> docDiffDetails;

}
