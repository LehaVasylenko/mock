package mock.config;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(1) // Чем меньше — тем раньше отрабатывает
public class GlobalRequestLogger implements ContainerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(GlobalRequestLogger.class);

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getRequestUri().toString();
        String headers = requestContext.getHeaders().toString();

        log.info("[INCOMING REQUEST] {} {}\nHeaders: {}\n", method, path, headers);
    }
}
