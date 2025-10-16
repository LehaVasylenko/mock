package mock.controller.other;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import mock.model.automation.RequestBody;
import mock.service.automation.AutomationService;

@Path("/auto")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class AutomationController {

    @Inject
    AutomationService automationService;

    @GET
    @Path("/token")
    public Uni<String> getToken() {
        return automationService.getToken();
    }

    @POST
    @Path("/newUser")
    public Uni<RequestBody> newUser(RequestBody r) {
        automationService.setRequestBody(r);
        return Uni.createFrom().item(r);
    }
}
