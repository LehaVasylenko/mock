package mock.controller.other;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import mock.service.other.EchoService;

@Path("/debug")
@Singleton
@Consumes(MediaType.WILDCARD)
@Produces(MediaType.APPLICATION_JSON)
public class EchoController {

    @Inject
    EchoService echoService;

    @POST
    @Path("/echo")
    public Uni<Response> echo(@Context HttpHeaders headers,
                              @Context UriInfo uri,
                              @Context SecurityContext sec,
                              byte[] body) {
        return echoService.doEcho(headers, uri, sec, body)
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }
}

