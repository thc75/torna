package cn.torna.service.metersphere.v3.util;


import com.alibaba.fastjson.JSON;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    public static String toJSONString(Object value) {
        try {
            return JSON.toJSONString(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] toJSONBytes(Object value) {
        try {
            return toJSONString(value).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> List<T> parseArray(Object content, Class<T> clazz) {
        try {
            // 使用 Gson 将 JSON 字符串转换为 List<T>
            return JSON.parseArray(String.valueOf(content), clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map parseMap(String jsonObject) {
        try {
            return JSON.parseObject(jsonObject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultHolder getResult(String jsonObject) {
        try {
            return JSON.parseObject(jsonObject, ResultHolder.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
