package cn.torna.common.util.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 处理Map反序列化
 */
public class MapTypeAdapter extends TypeAdapter<Object> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (Map.class.isAssignableFrom(type.getRawType())) {
                return (TypeAdapter<T>) new MapTypeAdapter();
            }
            return null;
        }
    };

    @Override
    public Object read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        switch (token) {
            case BEGIN_ARRAY:
                List<Object> list = new ArrayList<Object>();
                in.beginArray();
                while (in.hasNext()) {
                    list.add(read(in));
                }
                in.endArray();
                return list;

            case BEGIN_OBJECT:
                Map<String, Object> map = new LinkedTreeMap<String, Object>();
                in.beginObject();
                while (in.hasNext()) {
                    map.put(in.nextName(), read(in));
                }
                in.endObject();
                return map;

            case STRING:
                return in.nextString();

            case NUMBER:
                // 解决反序列化整形变double

                double num = in.nextDouble();
                if (Math.abs(num) <= Integer.MAX_VALUE) {
                    // 判断数字是否为int
                    int intNum = (int) num;
                    if (num == intNum) {
                        return intNum;
                    } else {
                        return num;
                    }
                }

                // 数字超过long的最大值，返回浮点类型
                if (Math.abs(num) > Long.MAX_VALUE) {
                    return num;
                }

                // 判断数字是否为long
                long longNum = (long) num;
                if (num == longNum) {
                    return longNum;
                } else {
                    return num;
                }
            case BOOLEAN:
                return in.nextBoolean();

            case NULL:
                in.nextNull();
                return null;

            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void write(JsonWriter out, Object value) {

    }
}