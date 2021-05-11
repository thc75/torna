package cn.torna.sdk.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsonUtil {
    
    /**
     * 对象转json
     * @param obj
     * @return
     */
    public static String toJSONString(Object obj) {
        if(obj == null) {
            return "{}";
        }
        return JSON.toJSONString(obj);
    }

    /**
     * json转对象
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("json内容错误, class:" + clazz.getName() + ", msg:" + e.getMessage() + ", json:" + json);
        }
    }

    public static JSONObject parseJSONObject(String json) {
        return JSON.parseObject(json);
    }
}
