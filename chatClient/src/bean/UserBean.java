package bean;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;

import java.io.Serializable;

public class UserBean implements Serializable {
    private String userId;
    private String userName;
    private String latestContactTime;
    private Label stateLabel;
    private Label nameLabel;
    private Pane contactPane;
    private VBox outWindow;
    private boolean displaying;

    public Label getNameLabel() {
        return nameLabel;
    }

    public void setNameLabel(Label nameLabel) {
        this.nameLabel = nameLabel;
    }

    public boolean isDisplaying() {
        return displaying;
    }

    public void setDisplaying(boolean displaying) {
        this.displaying = displaying;
    }

    public VBox getOutWindow() {
        return outWindow;
    }

    public void setOutWindow(VBox outWindow) {
        this.outWindow = outWindow;
    }

    public Pane getContactPane() {
        return contactPane;
    }

    public void setContactPane(Pane contactPane) {
        this.contactPane = contactPane;
    }

    public String getLatestContactTime() {
        return latestContactTime;
    }

    public void setLatestContactTime(String latestContactTime) {
        this.latestContactTime = latestContactTime;
    }

    public Label getStateLabel() {
        return stateLabel;
    }

    public void setStateLabel(Label stateLabel) {
        this.stateLabel = stateLabel;
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
}
