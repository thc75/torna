package torna.manager.doc.postman;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import org.springframework.stereotype.Service;
import torna.manager.doc.DocParser;
import torna.manager.doc.ParseConfig;
import torna.manager.doc.swagger.DocBean;

/**
 * @author tanghc
 */
@Service("postmanDocParser")
public class PostmanDocParser implements DocParser {
    @Override
    public DocBean parseJson(String json, ParseConfig config) {
        JSONObject docRoot = JSON.parseObject(json, Feature.OrderedField, Feature.DisableCircularReferenceDetect);
        JSONObject info = docRoot.getJSONObject("info");
        JSONArray item = docRoot.getJSONArray("item");
        this.getRequestItem(item);
        DocBean docBean = new DocBean();
        docBean.setTitle(info.getString("name"));
        return null;
    }

    private void getRequestItem(JSONArray item) {

    }
}
