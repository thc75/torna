package cn.torna.service.dto;

import cn.torna.common.support.IdCodec;
import cn.torna.service.dataid.EnumInfoDataId;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Data
public class EnumInfoDTO implements EnumInfoDataId {

    private static final String TPL_CONTENT = "%s:%s:%s";
    private static final String TPL = "%s:%s";

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /**
     * 枚举名称, 数据库字段：name
     */
    private String name;

    /**
     * 枚举说明, 数据库字段：description
     */
    private String description;

    /**
     * module.id, 数据库字段：module_id
     */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long moduleId;

    private List<EnumItemDTO> items;

    @Override
    public String buildDataId() {
        String content = items == null || items.isEmpty()
                ? description
                : items.stream()
                .map(enumItemDTO ->
                        String.format(TPL_CONTENT,
                                enumItemDTO.getName(), enumItemDTO.getType(), enumItemDTO.getValue()))
                .collect(Collectors.joining("|"));
        String data = String.format(TPL, moduleId, content);
        return DigestUtils.md5DigestAsHex(data.getBytes(StandardCharsets.UTF_8));
    }
}
