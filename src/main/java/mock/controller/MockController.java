package mock.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import mock.model.SetTableDTO;
import mock.model.error_response.ErrorResponse;
import mock.model.table_value.AllTablesResult;
import mock.service.FileReaderService;
import mock.service.TableService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/now/v1/table")
@Tag(name = "Mock ServiceNowRest", description = "Basic operations with tables (mocked ServiceNow API)")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MockController {
    private static final Logger log = LoggerFactory.getLogger(MockController.class);

    @Inject
    TableService tableService;
    @Inject
    ObjectMapper objectMapper;

    @GET
    @Path("/{tableName}")
    @Operation(summary = "Get all records from table", description = "Returns all records, optionally filtered via sysparm_query")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successful response",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AllTablesResult.class)
            )),
            @APIResponse(responseCode = "404", description = "Table not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    public Uni<Response> getTable(@Parameter(description = "Table name", required = true) @PathParam("tableName") String tableName,
                                  @Context UriInfo uriInfo) {
        return tableService
                .getAllTableData(tableName, uriInfo.getQueryParameters(), System.currentTimeMillis())
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @POST
    @Path("/{tableName}")
    @Operation(summary = "Create new record", description = "Adds new record to the specified table")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Successful response",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AllTablesResult.class)
                    )),
            @APIResponse(responseCode = "404", description = "Table not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @APIResponse(responseCode = "405", description = "Method not allowed for this table",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    public Uni<Response> postTable(@Parameter(description = "Table name", required = true) @PathParam("tableName") String tableName,
                                   @HeaderParam("Authorization") String authHeader,
                                   @RequestBody(
                                           required = true,
                                           description = "Data for new record",
                                           content = @Content(schema = @Schema(implementation = SetTableDTO.class))
                                   ) Object dto) {
        return tableService
                .postDataToTable(authHeader, tableName, getRequestBody(dto), System.currentTimeMillis())
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @PATCH
    @Path("/{tableName}/{sysId}")
    @Operation(summary = "Update record", description = "Updates a specific record in the table")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Successful response",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AllTablesResult.class)
                    )),
            @APIResponse(responseCode = "404", description = "Table not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @APIResponse(responseCode = "405", description = "Method not allowed for this table",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    public Uni<Response> patchTable(@Parameter(description = "Table name", required = true) @PathParam("tableName") String tableName,
                                    @Parameter(description = "sys_id of the record", required = true) @PathParam("sysId") String sysId,
                                    @HeaderParam("Authorization") String authHeader,
                                    @RequestBody(
                                            required = true,
                                            description = "Fields to update",
                                            content = @Content(schema = @Schema(implementation = SetTableDTO.class))
                                    )Object dto) {
        return tableService
                .patchDataToTable(authHeader, tableName, sysId, getRequestBody(dto), System.currentTimeMillis())
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @DELETE
    @Path("/{tableName}/{sysId}")
    @Operation(summary = "Delete record", description = "Deletes record by sys_id from the specified table")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Successful response",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AllTablesResult.class)
                    )),
            @APIResponse(responseCode = "404", description = "Table or record not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @APIResponse(responseCode = "405", description = "Method not allowed for this table",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    public Uni<Response> patchTable(@Parameter(description = "Table name", required = true) @PathParam("tableName") String tableName,
                                    @Parameter(description = "sys_id of the record", required = true) @PathParam("sysId") String sysId) {
        return tableService
                .deleteDataFromTable(tableName, sysId, System.currentTimeMillis())
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    private SetTableDTO getRequestBody(Object dto) {
        SetTableDTO setTableDTO = null;
        try {
            String possibleDto = objectMapper.writeValueAsString(dto);
            log.info("Received body: {}", possibleDto);

            setTableDTO = objectMapper.convertValue(dto, SetTableDTO.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return setTableDTO != null ? setTableDTO : new SetTableDTO();
    }
}
