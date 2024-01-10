package cn.torna.manager.doc.postman;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * @author tanghc
 */
@Data
public class Body {

    private String mode;
    private String raw;
    private List<Param> urlencoded;
    private List<Param> formdata;
    private JSONObject options;

}
