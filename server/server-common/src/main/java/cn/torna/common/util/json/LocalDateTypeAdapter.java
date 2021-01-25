package cn.torna.common.util.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 参考 com.google.gson.DefaultDateTypeAdapter
 *
 * @author tanghc
 */
public class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {

    private static final String DATETIME_PATTERN = "yyyy-MM-dd";

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() == LocalDate.class) {
                return (TypeAdapter<T>) new LocalDateTypeAdapter();
            }
            return null;
        }
    };

    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN)));
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String dateString = in.nextString();
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATETIME_PATTERN));
    }
}
