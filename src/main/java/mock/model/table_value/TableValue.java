package mock.model.table_value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import mock.model.CreationMode;
import mock.model.SetTableDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TableValue {
    @JsonProperty("u_long_field")
    private String uLongField;

    @JsonProperty("u_date_field")
    private String uDateField;

    @JsonProperty("sys_mod_count")
    private String sysModCount;

    @JsonProperty("u_float_field")
    private String uFloatField;

    @JsonProperty("u_string_field")
    private String uStringField;

    @JsonProperty("u_decimal_field")
    private String uDecimalField;

    @JsonProperty("sys_updated_on")
    private String sysUpdatedOn;

    @JsonProperty("sys_tags")
    private String sysTags;

    @JsonProperty("u_boolean_field")
    private String uBooleanField;

    @JsonProperty("sys_class_name")
    private String sysClassName;

    @JsonProperty("sys_id")
    private String sysId;

    @JsonProperty("sys_updated_by")
    private String sysUpdatedBy;

    @JsonProperty("sys_created_on")
    private String sysCreatedOn;

    @JsonProperty("u_integer_field")
    private String uIntegerField;

    @JsonProperty("u_string_utf8_field")
    private String uStringUtf8Field;

    @JsonProperty("sys_created_by")
    private String sysCreatedBy;

    @JsonProperty("u_color_field")
    private String uColorField;

    @JsonProperty("u_phone_field")
    private String uPhoneField;

    @JsonProperty("u_url_field")
    private String uUrlField;

    public String getuLongField() {
        return safeToString(uLongField);
    }

    public String getuDateField() {
        return safeToString(uDateField);
    }

    public String getSysModCount() {
        return safeToString(sysModCount);
    }

    public String getuFloatField() {
        return safeToString(uFloatField);
    }

    public String getuStringField() {
        return safeToString(uStringField);
    }

    public String getuDecimalField() {
        return safeToString(uDecimalField);
    }

    public String getSysUpdatedOn() {
        return safeToString(sysUpdatedOn);
    }

    public String getSysTags() {
        return safeToString(sysTags);
    }

    public String getuBooleanField() {
        return safeToString(uBooleanField);
    }

    public String getSysClassName() {
        return safeToString(sysClassName);
    }

    public String getSysId() {
        return safeToString(sysId);
    }

    public String getSysUpdatedBy() {
        return safeToString(sysUpdatedBy);
    }

    public String getSysCreatedOn() {
        return safeToString(sysCreatedOn);
    }

    public String getuIntegerField() {
        return safeToString(uIntegerField);
    }

    public String getuStringUtf8Field() {
        return safeToString(uStringUtf8Field);
    }

    public String getSysCreatedBy() {
        return safeToString(sysCreatedBy);
    }

    public String getuColorField() {
        return safeToString(uColorField);
    }

    public String getuPhoneField() {
        return safeToString(uPhoneField);
    }

    public String getuUrlField() {
        return safeToString(uUrlField);
    }

    public void setuLongField(String uLongField) {
        this.uLongField = uLongField;
    }

    public void setuDateField(String uDateField) {
        this.uDateField = uDateField;
    }

    public void setSysModCount(String sysModCount) {
        this.sysModCount = sysModCount;
    }

    public void setuFloatField(String uFloatField) {
        this.uFloatField = uFloatField;
    }

    public void setuStringField(String uStringField) {
        this.uStringField = uStringField;
    }

    public void setuDecimalField(String uDecimalField) {
        this.uDecimalField = uDecimalField;
    }

    public void setSysUpdatedOn(String sysUpdatedOn) {
        this.sysUpdatedOn = sysUpdatedOn;
    }

    public void setSysTags(String sysTags) {
        this.sysTags = sysTags;
    }

    public void setuBooleanField(String uBooleanField) {
        this.uBooleanField = uBooleanField;
    }

    public void setSysClassName(String sysClassName) {
        this.sysClassName = sysClassName;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public void setSysUpdatedBy(String sysUpdatedBy) {
        this.sysUpdatedBy = sysUpdatedBy;
    }

    public void setSysCreatedOn(String sysCreatedOn) {
        this.sysCreatedOn = sysCreatedOn;
    }

    public void setuIntegerField(String uIntegerField) {
        this.uIntegerField = uIntegerField;
    }

    public void setuStringUtf8Field(String uStringUtf8Field) {
        this.uStringUtf8Field = uStringUtf8Field;
    }

    public void setSysCreatedBy(String sysCreatedBy) {
        this.sysCreatedBy = sysCreatedBy;
    }

    public void setuColorField(String uColorField) {
        this.uColorField = uColorField;
    }

    public void setuPhoneField(String uPhoneField) {
        this.uPhoneField = uPhoneField;
    }

    public void setuUrlField(String uUrlField) {
        this.uUrlField = uUrlField;
    }

    public TableValue() {
    }

    public TableValue(String uLongField, String uDateField, String sysModCount, String uFloatField, String uStringField, String uDecimalField, String sysUpdatedOn, String sysTags, String uBooleanField, String sysClassName, String sysId, String sysUpdatedBy, String sysCreatedOn, String uIntegerField, String uStringUtf8Field, String sysCreatedBy, String uColorField, String uPhoneField, String uUrlField) {
        this.uLongField = uLongField;
        this.uDateField = uDateField;
        this.sysModCount = sysModCount;
        this.uFloatField = uFloatField;
        this.uStringField = uStringField;
        this.uDecimalField = uDecimalField;
        this.sysUpdatedOn = sysUpdatedOn;
        this.sysTags = sysTags;
        this.uBooleanField = uBooleanField;
        this.sysClassName = sysClassName;
        this.sysId = sysId;
        this.sysUpdatedBy = sysUpdatedBy;
        this.sysCreatedOn = sysCreatedOn;
        this.uIntegerField = uIntegerField;
        this.uStringUtf8Field = uStringUtf8Field;
        this.sysCreatedBy = sysCreatedBy;
        this.uColorField = uColorField;
        this.uPhoneField = uPhoneField;
        this.uUrlField = uUrlField;
    }

    public TableValue(SetTableDTO dto, String auth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = LocalDateTime.now().format(formatter);

        this.sysId = UUID.randomUUID().toString().replace("-", "");
        this.sysUpdatedBy = auth;
        this.sysCreatedBy = auth;
        this.sysModCount = "0";
        this.sysUpdatedOn = formattedDate;
        this.sysCreatedOn = formattedDate;
        this.sysTags = "";
        this.sysClassName = "u_test_table";
        this.uLongField = safeToString(dto.getuLongField());
        this.uDateField = dto.getuDateField();
        this.uFloatField = safeToString(dto.getuFloatField());
        this.uStringField = dto.getuStringField();
        this.uDecimalField = safeToString(dto.getuDecimalField());
        this.uBooleanField = safeToString(dto.getuBooleanField());
        this.uIntegerField = safeToString(dto.getuIntegerField());
        this.uStringUtf8Field = dto.getuStringUtf8Field();
        this.uColorField = dto.getuColorField();
        this.uPhoneField = dto.getuPhoneField();
        this.uUrlField = dto.getuUrlField();
    }

    public static TableValue patched(TableValue v, SetTableDTO dto, String auth) {
        v.sysUpdatedOn = now();

        if (dto.getuLongField() != null)         v.uLongField = safeToString(dto.getuLongField());
        if (dto.getuDateField() != null)         v.uDateField = dto.getuDateField();
        if (dto.getuFloatField() != null)        v.uFloatField = safeToString(dto.getuFloatField());
        if (dto.getuStringField() != null)       v.uStringField = dto.getuStringField();
        if (dto.getuDecimalField() != null)      v.uDecimalField = safeToString(dto.getuDecimalField());
        if (dto.getuBooleanField() != null)      v.uBooleanField = safeToString(dto.getuBooleanField());
        if (dto.getuIntegerField() != null)      v.uIntegerField = safeToString(dto.getuIntegerField());
        if (dto.getuStringUtf8Field() != null)   v.uStringUtf8Field = dto.getuStringUtf8Field();
        if (dto.getuColorField() != null)        v.uColorField = dto.getuColorField();
        if (dto.getuPhoneField() != null)        v.uPhoneField = dto.getuPhoneField();
        if (dto.getuUrlField() != null)          v.uUrlField = dto.getuUrlField();

        return v;
    }

    @JsonIgnore
    private static String now() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    @JsonIgnore
    private static String safeToString(Object o) {
        return o != null ? o.toString() : "";
    }
}
