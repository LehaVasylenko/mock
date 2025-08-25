package mock.model.error_response;

import mock.model.IResponse;

public record ErrorResponse(ErrorBody error, String status) implements IResponse {
}
