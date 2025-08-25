package mock.service;


import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import mock.model.scripted_api.ScenarioResponseDTO;

import java.util.List;

@ApplicationScoped
public class ScenarioService {

    public Uni<Response> buildResponse(List<String> scenario, Object requestBody) {
        final List<String> finalScenario = (scenario == null || scenario.isEmpty()) ? List.of("default") : scenario;
        final Object finalRequestBody = (requestBody == null) ? "default" : requestBody;
        return Uni.createFrom().item(() -> {
            int status;
            String message;
            String statusStr;

            if (finalScenario.contains("default")) {
                status = 200;
                message = "This is a success response";
                statusStr = "ok";
            } else if (finalScenario.contains("okay")) {
                status = 201;
                message = "This is a response";
                statusStr = "okay";
            } else if (finalScenario.contains("nothing")) {
                status = 204;
                message = "This is a response";
                statusStr = "nothing";
            } else if (finalScenario.contains("error")) {
                status = 400;
                message = "This is an error response";
                statusStr = "error";
            } else if (finalScenario.contains("teapot")) {
                status = 418;
                message = "This is a teapot error";
                statusStr = "error";
            } else {
                status = 500;
                message = "Unknown scenario";
                statusStr = "internal server error";
            }

            if (status == 204) {
                return Response.status(status).build();
            }

            return Response.status(status)
                    .entity(new ScenarioResponseDTO(statusStr, finalScenario, finalRequestBody, message))
                    .build();
        });
    }
}

