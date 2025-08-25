package mock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Singleton
public class FileReaderService {
    private static final Logger log = LoggerFactory.getLogger(FileReaderService.class);

    private final ObjectMapper objectMapper;

    public FileReaderService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String formatJsonString(String filename) throws IOException {
        Object json = objectMapper.readValue(readFileAsString(filename), Object.class);
        return objectMapper.writeValueAsString(json);
    }

    public <T> T readFromJson(String filename, Class<T> clazz) throws IOException {
        Path filePath = getFilePath(filename);
        log.info(filePath.toAbsolutePath().toString());
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            return objectMapper.readValue(inputStream, clazz);
        }
    }

    private Path getFilePath(String tableName) {
        Path folder = getPathToDB();
        return folder.resolve(tableName + ".json");
    }

    private Path getPathToDB() {
        return Paths.get("db");
    }

    public String readFileAsString(String filename) throws IOException {
        Path filePath = getFilePath(filename);
        return new String(Files.readAllBytes(filePath));
    }

    public String readFileAsHtmlXmlString(String filename) throws IOException {
        Path filePath = getPathToDB().resolve(filename);
        return new String(Files.readAllBytes(filePath));
    }


}

