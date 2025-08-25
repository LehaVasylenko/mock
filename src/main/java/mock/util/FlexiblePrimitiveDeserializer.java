package mock.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class FlexiblePrimitiveDeserializer<T> extends JsonDeserializer<T> {

    private final Class<T> targetType;

    public FlexiblePrimitiveDeserializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.currentToken();

        try {
            if (targetType == String.class) {
                // просто вернуть строку
                return targetType.cast(p.getValueAsString(""));
            }

            String raw = p.getValueAsString("").trim();

            if (raw.isEmpty()) {
                return handleEmpty(); // вернуть "" или false/null
            }

            // --- Числа ---
            if (targetType == Long.class) {
                try {
                    return targetType.cast(Long.parseLong(raw));
                } catch (Exception e) {
                    return targetType.cast(null); // SNOW → ""
                }
            }

            if (targetType == Integer.class) {
                try {
                    return targetType.cast(Integer.parseInt(raw));
                } catch (Exception e) {
                    return targetType.cast(null);
                }
            }

            if (targetType == Double.class) {
                try {
                    return targetType.cast(Double.parseDouble(raw));
                } catch (Exception e) {
                    return targetType.cast(null);
                }
            }

            if (targetType == Float.class) {
                try {
                    return targetType.cast(Float.parseFloat(raw));
                } catch (Exception e) {
                    return targetType.cast(null);
                }
            }

            // --- Boolean ---
            if (targetType == Boolean.class) {
                String val = raw.toLowerCase();
                return targetType.cast(val.equals("true") || val.equals("1"));
            }

        } catch (Exception e) {
            return handleEmpty(); // крайний fallback
        }

        return null;
    }

    private T handleEmpty() {
        if (targetType == Boolean.class) return targetType.cast(false);
        else return null;
    }
}

