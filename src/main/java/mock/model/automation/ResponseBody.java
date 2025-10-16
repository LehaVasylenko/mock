package mock.model.automation;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResponseBody(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("expires_in")
        long expiresIn
) {
}
