package torna.manager.doc.postman;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import torna.manager.doc.DocParser;
import torna.manager.doc.ParseConfig;

/**
 * @author tanghc
 */
@Service("postmanDocParser")
public class PostmanDocParser implements DocParser<Postman> {

    @Override
    public Postman parseJson(String json, ParseConfig config) {
        return JSON.parseObject(json, Postman.class);
    }
}
