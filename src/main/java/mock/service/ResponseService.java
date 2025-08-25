package mock.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mock.config.DelayConfig;
import mock.model.IResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ResponseService {
    private static final Logger log = LoggerFactory.getLogger(ResponseService.class);

    @Inject
    HeaderService headerService;

    @Inject
    DelayConfig delayConfig;

    public Response buildResponseWithHeaders(Object body, int status, long startTime, int length, String path) {
        long endTime = startTime + delayConfig.getDelay();
        if (System.currentTimeMillis() <= endTime) {
            try {
                Thread.sleep(endTime - System.currentTimeMillis());
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
        return headerService
                .applyHeaders(Response.status(status).entity(body), length, path)
                .build();
    }
}
