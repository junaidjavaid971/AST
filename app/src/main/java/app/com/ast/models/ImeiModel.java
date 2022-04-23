package app.com.ast.models;

public class ImeiModel {
    public String imei, updatedOn, timestamp, name;

    public ImeiModel(String imei, String name, String updatedOn, String timestamp) {
        this.imei = imei;
        this.updatedOn = updatedOn;
        this.timestamp = timestamp;
        this.name = name;
    }

    public ImeiModel() {
    }
}
