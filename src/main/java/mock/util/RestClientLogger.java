package mock.util;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Base64;

@Provider
public class RestClientLogger implements ClientRequestFilter, ClientResponseFilter {

    private final String username = "admin";
    private final String password = "YU16jcHvc$=B";

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String token = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        requestContext.getHeaders().add("Authorization", "Basic " + token);
        System.out.println("➡️ Request: " + requestContext.getMethod() + " " + requestContext.getUri());
//        requestContext.getHeaders().forEach((k, v) -> System.out.println("➡️ Header: " + k + " = " + v));
//        if (requestContext.hasEntity()) {
//            System.out.println("➡️ Body: " + requestContext.getEntity());
//        }
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext)
            throws IOException {
        System.out.println("⬅️ Response status: " + responseContext.getStatus());
//        System.out.println("⬅️ Headers: " + responseContext.getHeaders());
        // Можно прочитать тело ответа, но осторожно — один раз!
    }
}
