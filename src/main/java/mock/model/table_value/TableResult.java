package mock.model.table_value;

import mock.model.IResponse;

public class TableResult implements IResponse {
    TableValue result;

    public TableResult() {
    }

    public TableResult(TableValue result) {
        this.result = result;
    }

    public TableValue getResult() {
        return result;
    }

    public void setResult(TableValue result) {
        this.result = result;
    }
}
