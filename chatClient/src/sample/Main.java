package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import util.SocketUtil;
import util.StageUtil;

import java.util.concurrent.TimeUnit;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("烟大IM  -登录");
        primaryStage.getIcons().add(new Image("sample/icon.png"));
        primaryStage.setScene(new Scene(root, 428, 620));
        primaryStage.setResizable(false);
        StageUtil.setPrimaryStage(primaryStage);
        primaryStage.show();
        SocketUtil.setServerSocket();
        Thread thread = new Thread(() -> {
            try {
                while (true) {//heart beats
                    TimeUnit.MINUTES.sleep(4);
                    SocketUtil.sendMessage("hEaRtBEatS");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        thread.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
