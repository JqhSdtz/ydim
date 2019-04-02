package util;

import bean.UserBean;
import org.json.JSONObject;
import sample.ChatController;


public class LogUtil {

    private static String token;

    public static UserBean clientUser;

    public static String getToken() {
        return token;
    }

    public static UserBean login(String uid, String pwd) {
        try{
            JSONObject json = new JSONObject();
            json.put("login", true);
            json.put("uid", uid);
            json.put("pwd", pwd);
            SocketUtil.sendMessage(json.toString());
            String str = SocketUtil.getMessage();
            json = new JSONObject(str);
            int state = json.getInt("state");
            if(state == -1)
                return null;
            UserBean user = new UserBean();
            if(state == -2)
                user.setUserId("");
            else if(state == 1){
                user.setUserId(uid);
                user.setUserName(json.getString("name"));
                token = json.getString("token");
                ChatController.setLoginJson(json);
            }
            return user;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
