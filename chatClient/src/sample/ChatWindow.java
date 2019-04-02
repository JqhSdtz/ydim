package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import util.ControllerManager;
import util.LogUtil;
import util.SocketUtil;
import util.StageUtil;


public class ChatWindow extends Application {
    private Stage stage=new Stage();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("chat.fxml"));
        primaryStage.setTitle("烟大IM   -当前用户 " + LogUtil.clientUser.getUserName());
        primaryStage.getIcons().add(new Image("sample/icon.png"));
        primaryStage.setScene(new Scene(root, 860, 560));
        primaryStage.setResizable(false);
        StageUtil.changeStage(primaryStage);
        primaryStage.setOnCloseRequest(event -> {
            try{
                ControllerManager.chatController.onClose();
                ControllerManager.loginController.onClose();
            }catch (Exception e){
                e.printStackTrace();
            }
            primaryStage.close();
        });
        SocketUtil.setChatSocket();
    }

    public static void main(String[] args) {
        launch(args);
    }

    void showWindow() throws Exception {
        start(stage);
    }

}
