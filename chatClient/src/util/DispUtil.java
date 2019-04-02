package util;

import bean.UserBean;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import sample.ChatController;


public class DispUtil {
    public static String offlineMessage = "对方当前离线，上线后将收到消息";

    public static void onlineNotify(UserBean user) {
        Label stateLabel = user.getStateLabel();
        stateLabel.setText("在线");
        stateLabel.setStyle("-fx-text-fill: rgb(200,150,100)");
    }

    public static void offlineNotify(UserBean user) {
        Label stateLabel = user.getStateLabel();
        stateLabel.setText("离线");
        stateLabel.setStyle("-fx-text-fill: black");
    }

    public static void onMessageRemind(UserBean user) {
        Label nameLabel = user.getNameLabel();
        nameLabel.setStyle("-fx-text-fill: rgb(200,100,100)");
    }

    public static void onContactDisplay(ScrollPane outWindowPane, UserBean user) {
        if (user.isDisplaying())
            return;
        user.setDisplaying(true);
        UserBean curContact = ChatController.curDisplayingContact;
        if (curContact != null) {
            curContact.setDisplaying(false);
            //curContact.getOutWindow().setVisible(false);
            Label curNameLabel = curContact.getNameLabel();
            curNameLabel.setStyle("-fx-text-fill: black");
        }
        Label nameLabel = user.getNameLabel();
        nameLabel.setStyle("-fx-text-fill: rgb(200,150,100)");
        //user.getOutWindow().setVisible(true);
        outWindowPane.setContent(user.getOutWindow());
        ChatController.preDisplayingContact = ChatController.curDisplayingContact;
        ChatController.curDisplayingContact = user;
    }
}
