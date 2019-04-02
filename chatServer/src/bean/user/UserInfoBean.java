package bean.user;

public class UserInfoBean {
    private String profileIconUrl;
    private String intro;

    public UserInfoBean() {
    }

    public UserInfoBean(String profileIconUrl, String intro) {
        this.profileIconUrl = profileIconUrl;
        this.intro = intro;
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
}
