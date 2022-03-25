package cn.torna.common.support;

import cn.torna.common.util.IdUtil;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.CollectionCodec;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.TypeUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 隐藏真实id，转换成hashid
 *
 * @author tanghc
 */
public class IdCodec implements ObjectSerializer, ObjectDeserializer {

    private static final CollectionCodec COLLECTION_CODEC = new CollectionCodec();


    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        Class<?> clazz = TypeUtils.getClass(fieldType);
        if (List.class.isAssignableFrom(clazz)) {
            List list = (List) object;
            if (CollectionUtils.isEmpty(list)) {
                out.writeNull(SerializerFeature.WriteNullListAsEmpty);
            } else {
                List<String> hashIds = (List<String>) list.stream()
                        .map(el -> IdUtil.encode(Long.parseLong(String.valueOf(el))))
                        .collect(Collectors.toList());
                out.write(hashIds);
            }
        } else {
            Number value = (Number) object;
            if (value == null) {
                out.writeNull(SerializerFeature.WriteNullNumberAsZero);
                return;
            }
            // 转换成hashid
            out.writeString(IdUtil.encode(value.longValue()));
        }
    }


    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        final JSONLexer lexer = parser.lexer;
        Class<?> clazz = TypeUtils.getClass(type);
        // 将hashid解析成真实id
        if (Collection.class.isAssignableFrom(clazz)) {
            List list = COLLECTION_CODEC.deserialze(parser, List.class, fieldName);
            return (T) list.stream()
                    .map(hashId -> IdUtil.decode(String.valueOf(hashId)))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            String val = lexer.stringVal();
            lexer.nextToken(JSONToken.COMMA);
            Long decodeVal = IdUtil.decode(val);
            if (clazz == int.class || clazz == Integer.class) {
                if (decodeVal == null) {
                    return null;
                } else {
                    return (T) Integer.valueOf(decodeVal.intValue());
                }
            } else {
                return (T) decodeVal;
            }
        }
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}