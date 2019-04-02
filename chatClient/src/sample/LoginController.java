package sample;

import org.json.JSONObject;
import util.ControllerManager;
import util.LogUtil;
import bean.UserBean;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import util.SocketUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField userName;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    private UserBean clientUser;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        ControllerManager.loginController = this;
        //userName.focusedProperty().addListener((observable, oldValue, newValue) -> {});
        registerButton.setDisable(true);
        loginButton.setOnMouseClicked((actionEvent) -> {
            String uid = userName.getText();
            String pwd = password.getText();
            UserBean user = LogUtil.login(uid, pwd);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(user == null){
                alert.setContentText("未知错误！您可以稍候再次尝试登陆");
                alert.setTitle("登录失败");
            }else if("".equals(user.getUserId())){
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("用户名或密码错误!");
                alert.setTitle("登录失败");
            }else {
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setContentText("登录成功！");
                alert.setTitle("登录成功");
                clientUser = user;
                LogUtil.clientUser = user;
                alert.setOnCloseRequest(event -> ChatController.showChatWindow(user));
            }
            alert.show();
        });
    }

    public void onClose() throws Exception {
        JSONObject json = new JSONObject();
        json.put("uid", clientUser.getUserId());
        json.put("token", LogUtil.getToken());
        json.put("close", true);
        SocketUtil.sendMessage(json.toString());
        SocketUtil.closeServerSocket();
    }
}
