package mock.controller;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mock.model.scripted_api.ScenarioResponseDTO;
import mock.service.ScenarioService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/1702979/my_function/")
@Tag(name = "Scripted Rest Mock", description = "Returns responses based on query/body scenarios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ScenarioMockController {

    @Inject
    ScenarioService service;

    @GET
    @Path("fun_run_get")
    @Operation(
            summary = "Mock GET endpoint",
            description = "Returns a mocked response based on the `scenario` query parameter"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Successful response",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class))
            ),
            @APIResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class))),
            @APIResponse(responseCode = "418", description = "Teapot",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class)))
    })
    public Uni<Response> get(@Parameter(description = "Scenario names to control behavior", example = "default,teapot") @QueryParam("scenario") List<String> scenario) {
        return service
                .buildResponse(scenario, null)
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @POST
    @Path("fun_run_post")
    @Operation(
            summary = "Mock POST endpoint",
            description = "Returns a mocked response based on the `scenario` query parameter"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Successful response",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class))
            ),
            @APIResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class))),
            @APIResponse(responseCode = "418", description = "Teapot",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class)))
    })
    public Uni<Response> post(@Parameter(description = "Scenario names to control behavior", example = "default,teapot") @QueryParam("scenario") List<String> scenario, Object body) {
        return service
                .buildResponse(scenario, body)
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @PUT
    @Path("fun_run_put")
    @Operation(
            summary = "Mock PUT endpoint",
            description = "Returns a mocked response based on the `scenario` query parameter"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Successful response",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class))
            ),
            @APIResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class))),
            @APIResponse(responseCode = "418", description = "Teapot",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class)))
    })
    public Uni<Response> put(@Parameter(description = "Scenario names to control behavior", example = "default,teapot") @QueryParam("scenario") List<String> scenario, Object body) {
        return service
                .buildResponse(scenario, body)
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @PATCH
    @Path("fun_run_patch")
    @Operation(
            summary = "Mock PATCH endpoint",
            description = "Returns a mocked response based on the `scenario` query parameter"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Successful response",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class))
            ),
            @APIResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class))),
            @APIResponse(responseCode = "418", description = "Teapot",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class)))
    })
    public Uni<Response> patch(@Parameter(description = "Scenario names to control behavior", example = "default,teapot") @QueryParam("scenario") List<String> scenario, Object body) {
        return service
                .buildResponse(scenario, body)
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @DELETE
    @Path("fun_run_delete")
    @Operation(
            summary = "Mock DELETE endpoint",
            description = "Returns a mocked response based on the `scenario` query parameter"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Successful response",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class))
            ),
            @APIResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class))),
            @APIResponse(responseCode = "418", description = "Teapot",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScenarioResponseDTO.class)))
    })
    public Uni<Response> delete(@Parameter(description = "Scenario names to control behavior", example = "default,teapot") @QueryParam("scenario") List<String> scenario) {
        return service
                .buildResponse(scenario, null)
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }
}
