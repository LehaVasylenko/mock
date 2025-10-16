package mock.client;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import mock.model.automation.RequestBody;
import mock.model.automation.ResponseBody;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "https://v6u1.iconductcloud.com:4433/api")
public interface AuthClient {

    @POST
    @Path("/Authentication/authorize")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Uni<ResponseBody> getAuth(RequestBody requestBody);

    @GET
    @Path("/users/organizations-by-user-name/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<String> getUserNameByEmail(@PathParam("email") String email);
}
