package mock.controller;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mock.config.DelayConfig;
import mock.model.error_response.ErrorResponse;
import mock.model.table_value.AllTablesResult;
import mock.service.TableService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

@Path("/api/v1/")
@Tag(name = "Utils for Mock ServiceNow Rest")
@Tag(name = "Utils for Mock ServiceNow Rest", description = "Utility endpoints to reset table data or configure artificial delay")
public class UtilController {
    @Inject
    TableService tableService;

    @Inject
    DelayConfig delayConfig;

    @GET
    @Path("/drop-table")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(
            summary = "Reset cached table data",
            description = "Clears all in-memory table data and reloads it from disk (if applicable)."
    )
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Table data reset successfully",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = SchemaType.STRING))
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Error occurred",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = SchemaType.STRING))
            )
    })
    public Uni<Response> dropTable() {
        return tableService
                .resetTable()
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @POST
    @Path("/set-delay")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Set artificial delay (in milliseconds)",
            description = "Applies a delay to all REST responses to simulate latency or slow network"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = Map.class,
                            example = "{\"delayMillis\": 500}"
                    ),
                    examples = {
                            @ExampleObject(
                                    name = "Set 500ms Delay",
                                    value = "{\"delayMillis\": 500}"
                            )
                    }
            )
    )
    @APIResponse(
            responseCode = "200",
            description = "Delay successfully set",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            example = "{\"status\": \"delay set\", \"delayMillis\": 500}"
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid input (delayMillis is missing or negative)"
    )
    public Response setDelay(Map<String, Long> body) {
        Long millis = body.get("delayMillis");
        if (millis != null && millis >= 0) {
            delayConfig.setDelay(millis);
            return Response.ok(Map.of("status", "delay set", "delayMillis", millis)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Path("/drop-delay")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Reset artificial delay",
            description = "Removes previously set delay. All responses will be immediate."
    )
    @APIResponse(
            responseCode = "200",
            description = "Delay reset",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            example = "{\"status\": \"delay reset\"}"
                    )
            )
    )
    public Response resetDelay() {
        delayConfig.reset();
        return Response.ok(Map.of("status", "delay reset")).build();
    }
}
