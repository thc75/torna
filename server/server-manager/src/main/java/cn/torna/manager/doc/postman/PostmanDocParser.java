package cn.torna.manager.doc.postman;

import cn.torna.manager.doc.DocParser;
import cn.torna.manager.doc.ParseConfig;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;

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
