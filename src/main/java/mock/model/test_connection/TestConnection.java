package mock.model.test_connection;


import mock.model.IResponse;

import java.util.List;
public class TestConnection implements IResponse {
    List<TestConnectionArray> result;

    public List<TestConnectionArray> getResult() {
        return result;
    }

    public TestConnection() {
    }

    public TestConnection(List<TestConnectionArray> result) {
        this.result = result;
    }

    public void setResult(List<TestConnectionArray> result) {
        this.result = result;
    }
}
