package cn.torna.manager.doc.postman;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author tanghc
 */
@Getter
@Setter
public class Body {

    private String mode;
    private String raw;
    private List<Param> urlencoded;
    private JSONObject options;

}
