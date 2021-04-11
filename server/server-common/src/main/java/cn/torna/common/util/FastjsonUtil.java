package cn.torna.common.util;

import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author tanghc
 */
public class FastjsonUtil {
    public static final SerializerFeature[] SERIALIZER_FEATURES = {
            SerializerFeature.WriteNullStringAsEmpty,
            SerializerFeature.WriteMapNullValue
    };

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

}
