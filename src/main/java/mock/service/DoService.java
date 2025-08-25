package mock.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import mock.config.DelayConfig;
import mock.model.error_response.ErrorBody;
import mock.model.error_response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ApplicationScoped
public class DoService {
    private static final Logger log = LoggerFactory.getLogger(DoService.class);

    @Inject
    FileReaderService fileReaderService;

    @Inject
    DelayConfig delayConfig;

    public Uni<Response> doDO(UriInfo uriInfo, String tableName, long startTime) {
        return Uni.createFrom().item(() -> {
            boolean isWsdl = uriInfo.getQueryParameters().containsKey("wsdl");
            log.info("wsdl inside: {}", isWsdl);
            try {
                String filename = isWsdl  ? tableName + ".xml" : tableName + ".html";
                log.info(filename);
                String content = fileReaderService.readFileAsHtmlXmlString(filename);

                MediaType mediaType = isWsdl ? MediaType.APPLICATION_XML_TYPE : MediaType.TEXT_HTML_TYPE;
                waitMethod(startTime);
                return Response.ok(content)
                        .type(mediaType)
                        .build();
            } catch (IOException e) {
                waitMethod(startTime);
                return Response.status(500)
                        .entity(new ErrorResponse(new ErrorBody("Internal Server Error", e.getMessage()),
                                            "failure"))
                        .build();
            }
        });
    }

    private void waitMethod(long startTime) {
        long endTime = startTime + delayConfig.getDelay();
        if (System.currentTimeMillis() <= endTime) {
            try {
                Thread.sleep(endTime - System.currentTimeMillis());
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }
}
