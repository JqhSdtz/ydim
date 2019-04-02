package util;

import javafx.stage.Stage;

public class StageUtil {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage primaryStage) {
        StageUtil.primaryStage = primaryStage;
    }

    public static void changeStage(Stage newStage){
        primaryStage.close();
        newStage.show();
        primaryStage = newStage;
    }
}
