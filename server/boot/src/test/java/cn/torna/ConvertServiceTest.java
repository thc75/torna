package cn.torna;

import cn.torna.manager.doc.postman.Postman;
import cn.torna.service.ConvertService;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author thc
 */
public class ConvertServiceTest extends TornaApplicationTests {

    @Autowired
    ConvertService convertService;

    @Test
    public void convertToPostman() {
        Postman postman = convertService.convertToPostman(9L);
        System.out.println(JSON.toJSONString(postman));;
    }

}
