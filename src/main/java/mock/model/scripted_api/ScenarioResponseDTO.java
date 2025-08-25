package mock.model.scripted_api;


import java.util.List;

public class ScenarioResponseDTO {
    private String status;
    private List<String> sc;
    private Object b; // body-scenario
    private String message;

    public ScenarioResponseDTO(String status, List<String> sc, Object b, String message) {
        this.status = status;
        this.sc = sc;
        this.b = b;
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSc(List<String> sc) {
        this.sc = sc;
    }

    public void setB(Object b) {
        this.b = b;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ScenarioResponseDTO() {
    }

    public String getStatus() { return status; }
    public List<String> getSc() { return sc; }
    public Object getB() { return b; }
    public String getMessage() { return message; }
}

