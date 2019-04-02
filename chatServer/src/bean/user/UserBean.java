package bean.user;

import json.JSONArray;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class UserBean implements Serializable {
    private static String idPattern = "^[0-9]{12}$";
    private static String pwdPattern = "^\\w{5,16}$";
    private static String namePattern = "^\\S{1,10}$";
    private static String pwdKey = "ytuJ.Q.hSD.TZ";
    private String userId;//帐号
    private String userName;//姓名
    private String password ;//密码
    private Timestamp latestContactTime;//作为联系人时，和用户最近一次的聊天时间
    private String profileIconUrl;
    private String intro;
    private JSONArray contactListJson;

    public UserBean() {}

    public UserBean(String userId, String userName, String password, Timestamp latestContactTime){
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.latestContactTime = latestContactTime;
    }

    public static String getPwdKey() {
        return pwdKey;
    }

    public static void setPwdKey(String pwdKey) {
        UserBean.pwdKey = pwdKey;
    }

    public static String getIdPattern() {
        return idPattern;
    }

    public static void setIdPattern(String idPattern) {
        UserBean.idPattern = idPattern;
    }

    public static String getPwdPattern() { return pwdPattern; }

    public static String getNamePattern() { return namePattern; }

    public JSONArray getContactListJson() {
        return contactListJson;
    }

    public void setContactListJson(JSONArray contactListJson) {
        this.contactListJson = contactListJson;
    }

    public String getProfileIconUrl() {
        return profileIconUrl;
    }

    public void setProfileIconUrl(String profileIconUrl) {
        this.profileIconUrl = profileIconUrl;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Timestamp getLatestContactTime() {
        return latestContactTime;
    }

    public void setLatestContactTime(Timestamp latestContactTime) {
        this.latestContactTime = latestContactTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserInfoBean getUserInfo(){ return new UserInfoBean(this.profileIconUrl, this.intro);}

    public void setUserInfo(UserInfoBean userInfo){
        this.profileIconUrl = userInfo.getProfileIconUrl();
        this.intro = userInfo.getIntro();
    }

}
