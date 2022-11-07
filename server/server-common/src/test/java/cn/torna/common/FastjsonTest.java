package cn.torna.common;

import cn.torna.common.support.IdCodec;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.Feature;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author tanghc
 */
public class FastjsonTest {

    private static final Feature[] FEATURES = {
            // 允许注释
            Feature.AllowComment,
            // 允许没有双引号
            Feature.AllowUnQuotedFieldNames,
            // 允许单引号
            Feature.AllowSingleQuotes,
            // 属性保持原来顺序
            Feature.OrderedField,
    };

    @Test
    public void testA() {
        A a = new A();
        a.setId(0);
        a.setName("jim");
        a.setIdList(Arrays.asList(1L, 2L, 3L));
        String json = JSON.toJSONString(a);
        System.out.println(json);

        A a1 = JSON.parseObject(json, A.class);
        System.out.println(a1);

        Assertions.assertEquals(a.id, a1.id);
        Assertions.assertEquals(a.idList, a1.idList);
    }

    @Test
    public void testB() {
        B b = new B();
        b.setUid(1123123);
        b.setPid(666L);
        String json = JSON.toJSONString(b);
        System.out.println(json);

        B b1 = JSON.parseObject(json, B.class);
        System.out.println(b1);

        Assertions.assertEquals(b.uid, b1.uid);
        Assertions.assertEquals(b.pid, b1.pid);
    }

    @Test
    public void testA2() {
        A a = new A();
        a.setName("jim");
        String json = JSON.toJSONString(a);
        System.out.println(json);

        A a1 = JSON.parseObject(json, A.class);
        System.out.println(a1);

        Assertions.assertEquals(a.name, a1.name);
    }

    @Data
    static class A {
        @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
        private Integer id;



        @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
        private List<Long> idList;

        private String name;
    }

    @Data
    static class B {
        @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
        private int uid;

        @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
        private long pid;
    }

    @Test
    public void parseArray() {
        String arr = "[\r\n\t{\r\n        //两个也可以只传其中一个\r\n        \"id\": 2,\r\n        \"acceptDocNo\": \"你好\"\r\n    }\r\n]";
        String obj = "\r\n\t{\r\n        //两个也可以只传其中一个\r\n        \"id\": 2,\r\n        \"acceptDocNo\": \"你好\"\r\n    }\r\n";
        System.out.println(arr);
//        String arr = "[{\"id\": 2,\"acceptDocNo\": \"你好\"}]";
        System.out.println(JSONValidator.from(arr).getType());
        System.out.println(JSONValidator.from(arr).getType() == JSONValidator.Type.Array);
        System.out.println(JSON.parseArray(arr));
        System.out.println(JSON.parseObject(obj));

        Object parse = JSON.parse(arr);
        System.out.println(parse);
        System.out.println(parse.getClass().getName());
        Object parse1 = JSON.parse(obj);
        System.out.println(parse1);
        System.out.println(parse1.getClass().getName());


//        String str = "{\"Body\":\"测试一下\\r\\n\"\n}";
//        JSONObject a = JSON.parseObject(str);
//        System.out.println(a);
    }




}
