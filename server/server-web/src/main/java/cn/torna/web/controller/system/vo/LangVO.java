package cn.torna.web.controller.system.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author thc
 */
@AllArgsConstructor
@Data
public class LangVO {

    private JSONObject content;

    private String defaultLang;

    private String userLang;

}
