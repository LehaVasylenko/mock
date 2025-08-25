package mock.model.scripted_api;

public class ScenarioRequestDTO {
    private String scenario;

    public String getScenario() {
        return scenario;
    }

    public ScenarioRequestDTO(String scenario) {
        this.scenario = scenario;
    }

    public ScenarioRequestDTO() {
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }
}
