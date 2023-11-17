package cn.torna.web.controller.doc.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author thc
 */
@Data
public class DocCompareVO {

    private JSONObject docOld;

    private JSONObject docNew;

}
