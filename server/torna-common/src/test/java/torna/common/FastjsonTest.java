package torna.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import torna.common.support.IdCodec;

import java.util.Arrays;
import java.util.List;

/**
 * @author tanghc
 */
public class FastjsonTest {

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

        Assert.assertEquals(a.id, a1.id);
        Assert.assertEquals(a.idList, a1.idList);
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

        Assert.assertEquals(b.uid, b1.uid);
        Assert.assertEquals(b.pid, b1.pid);
    }

    @Test
    public void testA2() {
        A a = new A();
        a.setName("jim");
        String json = JSON.toJSONString(a);
        System.out.println(json);

        A a1 = JSON.parseObject(json, A.class);
        System.out.println(a1);

        Assert.assertEquals(a.name, a1.name);
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



}
