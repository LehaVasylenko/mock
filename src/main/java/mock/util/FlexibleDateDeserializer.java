package mock.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FlexibleDateDeserializer extends JsonDeserializer<String> {

    private static final List<DateTimeFormatter> ACCEPTED_FORMATS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a") // AM/PM формат
    );

    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String raw = p.getValueAsString("").trim();
        if (raw.isEmpty()) return "";

        for (DateTimeFormatter fmt : ACCEPTED_FORMATS) {
            try {
                if (fmt.toString().contains("H") || fmt.toString().contains("h")) {
                    // формат с временем
                    LocalDateTime dt = LocalDateTime.parse(raw, fmt);
                    return dt.toLocalDate().format(OUTPUT_FORMAT);
                } else {
                    // только дата
                    LocalDate date = LocalDate.parse(raw, fmt);
                    return date.format(OUTPUT_FORMAT);
                }
            } catch (DateTimeParseException e) {
                // пробуем следующий формат
            }
        }

        return ""; // ничего не подошло
    }
}

