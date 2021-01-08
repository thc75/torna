package torna.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author tanghc
 */
public class FastjsonUtil {
    public static final SerializerFeature[] SERIALIZER_FEATURES = {SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteMapNullValue};
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";


    public static String toJSONString(Object obj) {
        return JSON.toJSONStringWithDateFormat(obj, DATE_FORMAT, SERIALIZER_FEATURES);
    }

}
