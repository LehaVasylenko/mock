package mock.client;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import mock.model.automation.RequestBody;
import mock.model.automation.ResponseBody;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "v6")
public interface AuthClient {

    @POST
    @Path("/Authentication/authorize")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<ResponseBody> getAuth(RequestBody requestBody);

    @GET
    @Path("/users/organizations-by-user-name/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<String> getUserNameByEmail(@QueryParam("email") String email);
}
