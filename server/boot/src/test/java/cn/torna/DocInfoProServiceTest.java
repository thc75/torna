package cn.torna;

import cn.torna.service.DocInfoProService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author thc
 */
public class DocInfoProServiceTest extends TornaApplicationTests{

    @Autowired
    DocInfoProService docInfoProService;

    @Test
    public void test() {
        List<String> userIds = docInfoProService.listSubscribeDocDingDingUserIds(2);
        System.out.println(userIds);
    }



}
