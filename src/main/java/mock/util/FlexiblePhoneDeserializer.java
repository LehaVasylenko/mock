package mock.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class FlexiblePhoneDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String raw = p.getValueAsString("").trim();
        if (raw.isEmpty()) return "";

        // Удалить пробелы, скобки, тире, точки
        String cleaned = raw.replaceAll("[\\s\\-().]", "");

        // Должен начинаться с +
        if (!cleaned.startsWith("+")) return "";

        String digitsOnly = cleaned.substring(1); // после +
        if (!digitsOnly.matches("\\d+")) return ""; // только цифры

        if (digitsOnly.length() < 12) return ""; // не меньше 12 цифр

        return "+" + digitsOnly;
    }
}

