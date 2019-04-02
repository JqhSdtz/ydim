package sample;

import bean.UserBean;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;
import util.ControllerManager;
import util.DispUtil;
import util.LogUtil;
import util.SocketUtil;

import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ChatController implements Initializable {
    private static UserBean clientUser;
    private static JSONObject loginJson = null;
    @FXML
    private VBox contactList;
    @FXML
    private Button addContactButton;
    @FXML
    private Pane addContactPane;
    @FXML
    private TextField addContactTextField;
    @FXML
    private Button addContactConfirmButton;
    @FXML
    private Button addContactCancelButton;
    @FXML
    private Label warningLabel;
    @FXML
    private Pane deleteContactPane;
    @FXML
    private Label deleteContactLabel;
    @FXML
    private Button deleteContactCancelButton;
    @FXML
    private Button deleteContactConfirmButton;
    @FXML
    private TextArea sendMessageTextArea;
    @FXML
    private Button sendMessageButton;
    @FXML
    private ScrollPane outWindowPane;
    @FXML
    private ScrollPane contactListContainer;
    @FXML
    private VBox initOutWindow;
    @FXML
    private Label welcomeLabel;
    private Map<String, UserBean> contactMap = new HashMap<>();
    public static UserBean curDisplayingContact = null;
    public static UserBean preDisplayingContact = null;
    private Map<String, ArrayList<JSONObject>> mapOfMessageQueue = new HashMap<>();

    public static UserBean getClientUser() {
        return clientUser;
    }

    static void showChatWindow(UserBean user) {
        ChatWindow chatWindow = new ChatWindow();
        try {
            clientUser = user;
            chatWindow.showWindow();
            JSONObject json = new JSONObject();
            json.put("uid", user.getUserId());
            json.put("token", LogUtil.getToken());
            json.put("open", true);
            SocketUtil.sendChatMessage(json.toString());
            Thread thread = new Thread(() -> {
                try {
                    while (true) {//heart beats
                        TimeUnit.MINUTES.sleep(4);
                        SocketUtil.sendChatMessage("hEaRtBEatS");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLoginJson(JSONObject json) {
        loginJson = json;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ControllerManager.chatController = this;
        initContactList();
        addContactButton.setOnMouseClicked(event -> addContactPane.setVisible(true));
        addContactCancelButton.setOnMouseClicked(event -> {
            addContactTextField.clear();
            addContactPane.setVisible(false);
        });
        deleteContactCancelButton.setOnMouseClicked(event -> deleteContactPane.setVisible(false));
        addContactConfirmButton.setOnMouseClicked(event -> {
            if (addContact(addContactTextField.getText()))
                warningLabel.setText("联系人添加成功！");
            else
                warningLabel.setText("联系人添加失败！");
        });
        sendMessageButton.setOnMouseClicked(event -> {
            sendChatMessage(sendMessageTextArea.getText());
            sendMessageTextArea.clear();
        });
        contactListContainer.setContent(contactList);
        String name = clientUser.getUserName();
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 10)
            welcomeLabel.setText("早上好，" + name);
        else if (hour < 13)
            welcomeLabel.setText("中午好，" + name);
        else if (hour < 19)
            welcomeLabel.setText("下午好，" + name);
        else
            welcomeLabel.setText("晚上好，" + name);
        outWindowPane.setContent(initOutWindow);
        //outWindowPane.setFitToHeight(true);
    }

    private UserBean getContactPane(JSONObject json) {
        Pane contactPane = new Pane();
        contactPane.setStyle("-fx-background-color: rgb(230,230,230)");
        contactPane.setPrefHeight(40);
        contactPane.setPrefWidth(225);
        UserBean contact = new UserBean();
        contact.setUserName(json.getString("userName"));
        Label name = new Label(contact.getUserName());
        name.setLayoutX(10);
        name.setLayoutY(10);
        contact.setNameLabel(name);
        String userId = json.getString("userId");
        contact.setUserId(userId);
        String userState = json.getString("state");
        Label state = new Label(userState);
        state.setLayoutX(120);
        state.setLayoutY(10);
        if ("在线".equals(userState))
            state.setStyle("-fx-text-fill: rgb(200,150,100);");
        contact.setStateLabel(state);
        Button deleteButton = new Button("删");
        deleteButton.setOnMouseClicked(event -> {
            deleteContactPane.setVisible(true);
            deleteContactLabel.setText("确定删除联系人\n”" + contact.getUserName() + "“吗？");
            deleteContactConfirmButton.setOnMouseClicked(event1 -> {
                if (deleteContact(contact.getUserId())) {
                    deleteContactLabel.setText("联系人”" + contact.getUserName() + "“\n删除成功！");
                    deleteContactConfirmButton.setOnMouseClicked(event2 -> {
                        deleteContactPane.setVisible(false);
                    });
                }
            });
        });
        deleteButton.setLayoutX(160);
        deleteButton.setLayoutY(5);
        deleteButton.setPrefWidth(20);
        deleteButton.setPrefHeight(20);
        contactPane.getChildren().add(deleteButton);
        contactPane.getChildren().add(name);
        contactPane.getChildren().add(state);
        VBox.setMargin(contactPane, new Insets(0, 0, 10, 0));
        contact.setContactPane(contactPane);
        VBox outWindow = new VBox();
        outWindow.setPrefWidth(660);
        outWindow.setPrefHeight(360);
        //outWindowPane.getChildren().add(outWindow);
        contact.setOutWindow(outWindow);
        contactPane.setOnMouseClicked(event -> DispUtil.onContactDisplay(outWindowPane, contact));
        return contact;
    }

    private void processMessageQueueOfContact(UserBean user) {
        if (mapOfMessageQueue.containsKey(user.getUserId())) {
            ArrayList<JSONObject> messageQueue = mapOfMessageQueue.get(user.getUserId());
            for (JSONObject jsonObject : messageQueue) {
                processMessage(jsonObject);
            }
            messageQueue.clear();
            mapOfMessageQueue.remove(user.getUserId());
        }
    }


    private void initContactList() {
        if (loginJson == null)
            return;
        JSONArray jsonArray = loginJson.getJSONArray("contactList");
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject json = jsonArray.getJSONObject(i);
            initContact(json);
        }
    }

    private boolean addContact(String contactId) {
        if (contactMap.containsKey(contactId))
            return false;
        JSONObject json = new JSONObject();
        json.put("token", LogUtil.getToken());
        json.put("addContact", true);
        json.put("uid", clientUser.getUserId());
        json.put("contactId", contactId);
        try {
            SocketUtil.sendMessage(json.toString());
            json = new JSONObject(SocketUtil.getMessage());
            if (json.has("Succeed")) {
                initContact(json);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void initContact(JSONObject json){
        UserBean contact = getContactPane(json);
        contactList.getChildren().add(contact.getContactPane());
        contactMap.put(contact.getUserId(), contact);
        processMessageQueueOfContact(contact);
    }

    private boolean deleteContact(String contactId) {
        if (!contactMap.containsKey(contactId))
            return false;
        JSONObject json = new JSONObject();
        json.put("token", LogUtil.getToken());
        json.put("deleteContact", true);
        json.put("uid", clientUser.getUserId());
        json.put("contactId", contactId);
        try {
            SocketUtil.sendMessage(json.toString());
            json = new JSONObject(SocketUtil.getMessage());
            if (json.has("Succeed")) {
                UserBean contact = contactMap.get(contactId);
                Pane contactPane = contact.getContactPane();
                contactList.getChildren().remove(contactPane);
                contact.getOutWindow().setVisible(false);
                contactMap.remove(contactId);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void sendChatMessage(String msg) {
        if ("".equals(msg) || curDisplayingContact == null)
            return;
        JSONObject json = new JSONObject();
        json.put("token", LogUtil.getToken());
        json.put("chat", true);
        json.put("uid", clientUser.getUserId());
        json.put("targetUserId", curDisplayingContact.getUserId());
        json.put("message", msg);
        try {
            SocketUtil.sendChatMessage(json.toString());
            if (curDisplayingContact == null || curDisplayingContact != preDisplayingContact)
                updateLatestContactTime(curDisplayingContact.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        displayMessage(curDisplayingContact.getOutWindow(), clientUser.getUserName()
                , msg, "own", null);
        reTop(curDisplayingContact.getContactPane());
    }

    private void reTop(Pane contactPane){
        int pos = contactList.getChildren().indexOf(contactPane);
        if (pos != 0) {
            contactList.getChildren().remove(pos);
            contactList.getChildren().add(0, contactPane);
        }
    }

    private void displayMessage(VBox outWindow, String userName, String msg, String source, String time) {
        if (time != null && !"".equals(time)) {
            Label timeLabel = new Label(time);
            outWindow.getChildren().add(timeLabel);
            timeLabel.setFont(new Font(20));
            if ("own".equals(source))
                VBox.setMargin(timeLabel, new Insets(0, 0, 0, 260));
        }
        Text msgText = new Text(userName + ": " + msg);
        Pane textPane = new Pane();
        msgText.setFont(new Font(20));
        msgText.setWrappingWidth(360);
        msgText.setX(20);
        msgText.setY(30);
        textPane.getChildren().add(msgText);
        double height = msgText.getLayoutBounds().getHeight();
        textPane.setMinHeight(height + 20);
        //textPane.setPadding(new Insets(20));
        outWindow.getChildren().add(textPane);
        Insets insets = null;
        if ("own".equals(source)) {
            textPane.setStyle("-fx-background-color: rgb(210,160,250)");
            insets = new Insets(7.5, 0, 7.5, 260);
        } else if ("opp".equals(source)) {
            textPane.setStyle("-fx-background-color: rgb(250,160,210)");
            insets = new Insets(7.5, 260, 7.5, 0);
        }
        VBox.setMargin(textPane, insets);
    }


    public void processMessageData(String msgData) {
        if ("".equals(msgData))
            return;
        JSONObject json = new JSONObject(msgData);
        if (json.has("offlineMessageArray")) {
            processMessageArray(json.getJSONArray("messageArray"));
            return;
        }
        processMessage(json);
    }

    private void processMessageArray(JSONArray msgArray) {
        for (int i = 0; i < msgArray.length(); ++i)
            processMessage(msgArray.getJSONObject(i));
    }

    private void processMessage(JSONObject json) {
        String contactId = json.getString("uid");
        if (!contactMap.containsKey(contactId)) {
            ArrayList<JSONObject> messageQueue;
            if (!mapOfMessageQueue.containsKey(contactId)) {
                messageQueue = new ArrayList<>();
                mapOfMessageQueue.put(contactId, messageQueue);
                messageQueue.add(json);
                addContact(contactId);
            } else {
                messageQueue = mapOfMessageQueue.get(contactId);
                messageQueue.add(json);
            }
            return;
        }
        dispatchMessage(contactId, json);
    }

    private void dispatchMessage(String contactId, JSONObject json) {
        UserBean contact = contactMap.get(contactId);
        if (json.has("onlineNotify")) {
            DispUtil.onlineNotify(contact);
        } else if (json.has("offlineNotify")) {
            DispUtil.offlineNotify(contact);
        } else if (json.has("offline")) {
            String msg = DispUtil.offlineMessage;
            displayMessage(contact.getOutWindow(), "系统消息"
                    , msg, "opp", null);
        } else {
            if (!contact.isDisplaying())
                DispUtil.onMessageRemind(contact);
            String time = json.has("time") ? json.getString("time") : null;
            displayMessage(contact.getOutWindow(), contact.getUserName(),
                    json.getString("message"), "opp", time);
        }
        reTop(contact.getContactPane());
    }

    private void updateLatestContactTime(String contactId) throws Exception {
        JSONObject json = new JSONObject();
        json.put("chat", true);
        json.put("updateContactTime", true);
        json.put("uid", clientUser.getUserId());
        json.put("token", LogUtil.getToken());
        json.put("targetUserId", contactId);
        SocketUtil.sendChatMessage(json.toString());
    }

    public void onMessage(String msg) {
        processMessageData(msg);
    }

    public void onClose() throws Exception {
        SocketUtil.closeChatSocket();
        JSONObject json = new JSONObject();
        json.put("uid", clientUser.getUserId());
        json.put("token", LogUtil.getToken());
        json.put("close", true);
        SocketUtil.sendChatMessage(json.toString());
    }
}
