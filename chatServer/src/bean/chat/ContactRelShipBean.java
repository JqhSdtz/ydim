package bean.chat;

import java.io.Serializable;
import java.sql.Timestamp;

public class ContactRelShipBean implements Serializable {
    private String uid1;
    private String uid2;
    private Timestamp latestContactTime;
    private int value;

    public ContactRelShipBean(){}

    public ContactRelShipBean(String uid1, String uid2, Timestamp latestContactTime){
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.latestContactTime = latestContactTime;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Timestamp getLatestContactTime() {
        return latestContactTime;
    }

    public void setLatestContactTime(Timestamp latestContactTime) {
        this.latestContactTime = latestContactTime;
    }

    public String getUid1() {
        return uid1;
    }

    public void setUid1(String uid1) {
        this.uid1 = uid1;
    }

    public String getUid2() {
        return uid2;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }

    /**返回true表示无需交换*/
    public static boolean compareUid(String uid1, String uid2){
        return uid1.compareTo(uid2) < 0;
    }
}
