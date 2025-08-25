package mock.model.table_value;

import mock.model.IResponse;

import java.util.List;

public class AllTablesResult implements IResponse {
    List<TableValue> result;

    public List<TableValue> getResult() {
        return result;
    }

    public AllTablesResult() {
    }

    public AllTablesResult(List<TableValue> result) {
        this.result = result;
    }

    public void setResult(List<TableValue> result) {
        this.result = result;
    }
}
