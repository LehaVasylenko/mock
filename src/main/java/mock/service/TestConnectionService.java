package mock.service;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class TestConnectionService {
    private static final Logger log = LoggerFactory.getLogger(TestConnectionService.class);

    @Inject
    FileReaderService readerService;

    @Inject
    ResponseService responseService;

    public Uni<Response> dotest() {
        return Uni.createFrom().item(() -> responseService.buildResponseWithHeaders(setCache(), 200, System.currentTimeMillis(), 0, ""));
    }

    private String setCache() {
        String result = "";
        try {
            result = readerService.formatJsonString("test_connection");
            log.info("Test connection successful");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return result;
    }
}
