package torna.common.util.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 参考 com.google.gson.DefaultDateTypeAdapter
 * @author tanghc
 */
public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {

    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @SuppressWarnings("unchecked")
        @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() == LocalDateTime.class) {
                return (TypeAdapter<T>) new LocalDateTimeTypeAdapter(gson);
            }
            return null;
        }
    };

    private Gson gson;

    public LocalDateTimeTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN)));
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String dateString = in.nextString();
        return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(DATETIME_PATTERN));
    }
}
