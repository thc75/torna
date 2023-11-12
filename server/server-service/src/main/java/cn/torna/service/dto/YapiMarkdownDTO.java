package cn.torna.service.dto;

import cn.torna.common.bean.User;
import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tanghc
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class YapiMarkdownDTO {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long projectId;

    /**
     * markdown文档内容, 数据库字段：content
     */
    private String content;

    private User user;

    private String ip;

}
