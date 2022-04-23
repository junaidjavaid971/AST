package app.com.ast.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "speedTestTable")
public class SpeedTest implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "pingtest")
    public String pingTest;


    @ColumnInfo(name = "downloadspeed")
    public String downloadSpeed;

    @ColumnInfo(name = "uploadspeed")
    public String uploadSpeed;
    @ColumnInfo(name = "testingMode")
    public String testingMode;
    @ColumnInfo(name = "testTime")
    public String testTime;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPingTest() {
        return pingTest;
    }

    public void setPingTest(String pingTest) {
        this.pingTest = pingTest;
    }

    public String getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(String downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public String getUploadSpeed() {
        return uploadSpeed;
    }

    public void setUploadSpeed(String uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }

    public String getTestingMode() {
        return testingMode;
    }

    public void setTestingMode(String testingMode) {
        this.testingMode = testingMode;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }
}
