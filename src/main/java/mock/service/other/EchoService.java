package mock.service.other;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import mock.config.DelayConfig;
import mock.service.DoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class EchoService {
    private static final Logger log = LoggerFactory.getLogger(EchoService.class);

    @Inject
    DelayConfig delayConfig;

    public Uni<Response> doEcho(HttpHeaders headers, UriInfo uri, SecurityContext sec, byte[] body, long startTime) {
        return Uni.createFrom().item(() -> {
            //headers
            Map<String, List<String>> hdrs = headers.getRequestHeaders()
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> new ArrayList<>(e.getValue())
                    ));

            //status
            String statusParam = uri.getQueryParameters().getFirst("status");
            int status = 200; // дефолт
            if (statusParam != null) {
                try {
                    status = Integer.parseInt(statusParam);
                } catch (NumberFormatException ignore) {
                }
            }

            //content-type
            String contentType = Optional.ofNullable(headers.getHeaderString(HttpHeaders.CONTENT_TYPE))
                    .orElse("application/octet-stream");

            // text if possible
            String bodyText = tryDecodeToText(body, contentType);
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            String formattedDate = LocalDateTime.now().format(formatter);
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("method", "POST");
            out.put("time", LocalDateTime.now());
            out.put("user", sec.getUserPrincipal().getName());
            out.put("path", uri.getPath());
            out.put("absoluteUri", uri.getRequestUri().toString());
            out.put("query", uri.getQueryParameters());
            out.put("headers", hdrs);
            out.put("contentType", contentType);
            out.put("bodyLength", body == null ? 0 : body.length);
            out.put("bodyText", bodyText);
            out.put("bodyBase64", body == null ? "" : Base64.getEncoder().encodeToString(body));

            waitMethod(startTime);
            return Response.status(status)
                    .entity(out)
                    .build();
        });
    }

    private String tryDecodeToText(byte[] body, String contentType) {
        if (body == null || body.length == 0) return "";
        //regular formats
        boolean looksTextual = contentType.startsWith("text/")
                || contentType.contains("json")
                || contentType.contains("xml")
                || contentType.contains("+json")
                || contentType.contains("+xml");
        if (!looksTextual) return null; //binary

        try {
            return new String(body, StandardCharsets.UTF_8);
        } catch (Exception ignore) {
            return null;
        }
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
