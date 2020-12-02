package torna.common.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * json工具类，兼容fastjson用法（非100%兼容，只封装了常用方法）
 */
public class JsonUtil {

    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * Gson实例线程安全，声明一个即可。文档中有说明：
     * Gson instances are Thread-safe so you can reuse them freely across multiple threads
     */
    private static Gson writer = new GsonBuilder()
            .registerTypeAdapterFactory(LocalDateTimeTypeAdapter.FACTORY)
            .registerTypeAdapterFactory(LocalDateTypeAdapter.FACTORY)
            .setDateFormat(DATETIME_PATTERN)
            .create();

    /**
     * 解析专用
     */
    private static Gson parser = new GsonBuilder()
            .registerTypeAdapterFactory(LocalDateTimeTypeAdapter.FACTORY)
            .registerTypeAdapterFactory(LocalDateTypeAdapter.FACTORY)
            // 解决int变double问题
            .registerTypeAdapterFactory(MapTypeAdapter.FACTORY)
            .setDateFormat(DATETIME_PATTERN)
            .create();

    private static final Type TYPE_OBJECT_MAP = new TypeToken<Map<String, Object>>() {
    }.getType();

    /**
     * 将对象转换成json
     *
     * @param object 对象
     * @return 返回json
     */
    public static String toJSONString(Object object) {
        return writer.toJson(object);
    }

    /**
     * 将json转成Map，key为String，value可能为LinkedHashMap/ArrayList/基础值。
     *
     * @param json json字符串
     * @return 返回map
     */
    public static Map<String, Object> parseObject(String json) {
        return parser.fromJson(json, TYPE_OBJECT_MAP);
    }


    /**
     * 将json转换成对象
     *
     * @param json  json
     * @param clazz 对象class
     * @param <T>   对象类型
     * @return 返回对象实例
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        return parser.fromJson(json, clazz);
    }

    /**
     * 将数组转成对象，[{...},{...}]
     *
     * @param json  json数组
     * @param clazz 对象class
     * @param <T>   对象类型
     * @return 返回List
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        Type type = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
        return parser.fromJson(json, type);
    }

    /**
     * 判断是否是一个有效的json，必须以"{"开头，"}"结尾
     *
     * @param json json
     * @return true，有效
     */
    public static boolean isGoodJson(String json) {
        if (json == null || "".equals(json)) {
            return false;
        }
        if (!(json.startsWith("{") && json.endsWith("}"))) {
            return false;
        }
        try {
            JsonParser.parseString(json);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }

    public static class ParameterizedTypeImpl implements ParameterizedType {
        private final Class raw;
        private final Type[] args;

        /**
         * @param raw  泛型擦除后的类型
         * @param args 泛型中的实际类型
         */
        public ParameterizedTypeImpl(Class raw, Type[] args) {
            this.raw = raw;
            this.args = args != null ? args : new Type[0];
        }

        @Override
        public Type[] getActualTypeArguments() {
            return args;
        }

        @Override
        public Type getRawType() {
            return raw;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }


}
