package mock.model.test_connection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InnerObject {
    String link;
    String value;

    public InnerObject(String link, String value) {
        this.link = link;
        this.value = value;
    }

    public String getLink() {
        return link;
    }

    public InnerObject() {
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
