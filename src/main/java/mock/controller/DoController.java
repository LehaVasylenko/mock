package mock.controller;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import mock.service.DoService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/")
@Tag(name = "Static data controller")
public class DoController {
    @Inject
    DoService doService;

    @GET
    @Path("/{tableName}.do")
    @Operation(
            summary = "Serve /u_test_table.do endpoint as HTML or WSDL",
            description = "If `wsdl` query parameter is present, returns XML (WSDL). Otherwise returns HTML form page."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "HTML form page or WSDL XML document",
                    content = {
                            @Content(mediaType = "text/html"),
                            @Content(mediaType = "application/xml")
                    }
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Table not found"
            )
    })
    public Uni<Response> getContent(@Parameter(description = "Add ?wsdl to get WSDL XML") @Context UriInfo uriInfo,
                                    @Parameter(description = "Table name", required = true) @PathParam("tableName") String tableName) {
        return doService
                .doDO(uriInfo, tableName, System.currentTimeMillis())
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }
}
