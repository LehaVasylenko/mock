package mock.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import mock.util.*;

public class SetTableDTO {
    @JsonDeserialize(using = FlexibleInteger.class)
    @JsonProperty("u_integer_field")
    private Integer uIntegerField;

    @JsonDeserialize(using = FlexibleDouble.class)
    @JsonProperty("u_float_field")
    private Double uFloatField;

    @JsonDeserialize(using = FlexibleStringDeserializer.class)
    @JsonProperty("u_string_field")
    private String uStringField;

    @JsonDeserialize(using = FlexibleBoolean.class)
    @JsonProperty("u_boolean_field")
    private Boolean uBooleanField;

    @JsonDeserialize(using = FlexibleFloat.class)
    @JsonProperty("u_decimal_field")
    private Float uDecimalField;

    @JsonDeserialize(using = FlexibleStringDeserializer.class)
    @JsonProperty("u_color_field")
    private String uColorField;

    @JsonDeserialize(using = FlexibleDateDeserializer.class)
    @JsonProperty("u_date_field")
    private String uDateField;

    @JsonDeserialize(using = FlexibleLong.class)
    @JsonProperty("u_long_field")
    private Long uLongField;

    @JsonDeserialize(using = FlexibleStringDeserializer.class)
    @JsonProperty("u_url_field")
    private String uUrlField;

    @JsonDeserialize(using = FlexiblePhoneDeserializer.class)
    @JsonProperty("u_phone_field")
    private String uPhoneField;

    @JsonDeserialize(using = FlexibleStringDeserializer.class)
    @JsonProperty("u_string_utf8_field")
    private String uStringUtf8Field;

    public SetTableDTO() {
    }

    public Integer getuIntegerField() {
        return uIntegerField;
    }

    public void setuIntegerField(Integer uIntegerField) {
        this.uIntegerField = uIntegerField;
    }

    public Double getuFloatField() {
        return uFloatField;
    }

    public void setuFloatField(Double uFloatField) {
        this.uFloatField = uFloatField;
    }

    public String getuStringField() {
        return uStringField;
    }

    public void setuStringField(String uStringField) {
        this.uStringField = uStringField;
    }

    public Boolean getuBooleanField() {
        return uBooleanField;
    }

    public void setuBooleanField(Boolean uBooleanField) {
        this.uBooleanField = uBooleanField;
    }

    public Float getuDecimalField() {
        return uDecimalField;
    }

    public void setuDecimalField(Float uDecimalField) {
        this.uDecimalField = uDecimalField;
    }

    public String getuColorField() {
        return uColorField;
    }

    public void setuColorField(String uColorField) {
        this.uColorField = uColorField;
    }

    public String getuDateField() {
        return uDateField;
    }

    public void setuDateField(String uDateField) {
        this.uDateField = uDateField;
    }

    public Long getuLongField() {
        return uLongField;
    }

    public void setuLongField(Long uLongField) {
        this.uLongField = uLongField;
    }

    public String getuUrlField() {
        return uUrlField;
    }

    public void setuUrlField(String uUrlField) {
        this.uUrlField = uUrlField;
    }

    public String getuPhoneField() {
        return uPhoneField;
    }

    public void setuPhoneField(String uPhoneField) {
        this.uPhoneField = uPhoneField;
    }

    public String getuStringUtf8Field() {
        return uStringUtf8Field;
    }

    public void setuStringUtf8Field(String uStringUtf8Field) {
        this.uStringUtf8Field = uStringUtf8Field;
    }

    public SetTableDTO(Integer uIntegerField, Double uFloatField, String uStringField, Boolean uBooleanField, Float uDecimalField, String uColorField, String uDateField, Long uLongField, String uUrlField, String uPhoneField, String uStringUtf8Field) {
        this.uIntegerField = uIntegerField;
        this.uFloatField = uFloatField;
        this.uStringField = uStringField;
        this.uBooleanField = uBooleanField;
        this.uDecimalField = uDecimalField;
        this.uColorField = uColorField;
        this.uDateField = uDateField;
        this.uLongField = uLongField;
        this.uUrlField = uUrlField;
        this.uPhoneField = uPhoneField;
        this.uStringUtf8Field = uStringUtf8Field;
    }
}
