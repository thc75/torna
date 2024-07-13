package cn.torna.common.bean;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author 六如
 */
@Data
public class TreeData {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    private String label;

    private String parentId;

    private List<TreeData> children;

}
