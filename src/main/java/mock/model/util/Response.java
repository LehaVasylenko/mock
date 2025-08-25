package mock.model.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    private List<UtilResult> result;

    public Response() {
    }

    public Response(List<UtilResult> result) {
        this.result = result;
    }

    public List<UtilResult> getResult() {
        return result;
    }

    public void setResult(List<UtilResult> result) {
        this.result = result;
    }
}
