package cn.torna.common;

import cn.torna.common.util.MockUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;

/**
 * @author thc
 */
public class MockUtilTest {

    @Test
    public void test() throws ScriptException {
        String mock = "var data = Mock.mock({\n" +
                "   // 20条数据\n" +
                "   \"data|20\": [{\n" +
                "     // 商品种类\n" +
                "     \"goodsClass\": \"女装\",\n" +
                "     // 商品Id\n" +
                "     \"goodsId|+1\": 1,\n" +
                "     // 商品名称\n" +
                "     \"goodsName\": \"@ctitle(10)\",\n" +
                "     // 商品地址\n" +
                "     \"goodsAddress\": \"@county(true)\",\n" +
                "     // 商品等级评价★\n" +
                "     \"goodsStar|1-5\": \"★\",\n" +
                "     // 商品图片\n" +
                "     \"goodsImg\": \"@Image('100x100','@color','小甜甜')\",\n" +
                "     \"addTime\": moment().format(),\n" +
                "     // 商品售价\n" +
                "     \"goodsSale|30-500\": 30\n" +
                "   }]\n" +
                " })\n" +
                "// 最终返回数据\n" +
                "return data;";

        Object data = MockUtil.mock(mock);
        System.out.println(data);
        Object data2 = MockUtil.mock(mock);
        System.out.println(data2);
//        System.out.println(JSON.toJSONString(data, SerializerFeature.PrettyFormat));
    }

}
