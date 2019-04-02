package util;

import bean.user.UserBean;
import dao.mybatisImpl.MybatisDaoFactory;
import dao.productor.user.UserDao;
import json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class UserUtil {
    public static ConcurrentHashMap<String, String> userTokenMap = new ConcurrentHashMap<>();//!!!

    public static UserBean login(String uid, String pwd) {
        String path = "http://www.lib.ytu.edu.cn:9999/webservice/WebService.asmx/verify_ReaderInfo";
        try {
            URL url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            String param = "{name:'" + uid + "',pw:'" + pwd + "'}";
            httpURLConnection.connect();
            OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream(), StandardCharsets.UTF_8);
            out.write(param);
            out.flush();
            int resultCode = httpURLConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == resultCode) {
                StringBuilder sb = new StringBuilder();
                String readLine;
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine);
                }
                responseReader.close();
                JSONObject json = new JSONObject(unescape(sb.toString())).getJSONObject("d");
                UserBean user = new UserBean();
                if(json.getBoolean("valid")){
                    user.setUserId(uid);
                    user.setUserName(json.getString("name"));
                    UserDao userDao = new MybatisDaoFactory().createUserDao();
                    if(userDao.checkUserId(user.getUserId())){//用户存在
                        user.setContactListJson(ContactUtil.getContactsList(user.getUserId()));
                    }else{//用户不存在
                        user.setPassword("");
                        userDao.createUser(user);
                    }
                }else{
                    user.setUserId("");
                }
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String unescape(String str){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < str.length(); ++i){
            char ch = str.charAt(i);
            if(ch == '\\')
                continue;
            if(ch == '"' && (str.charAt(i - 1 ) == '}' || str.charAt(i + 1) == '{'))
                continue;
            sb.append(ch);
        }
        return sb.toString();
    }

    public static String getUserToken(String uid, String pwd){
        return AESUtil.encrypt(uid + pwd + (int) (Math.random() * 10000), AESUtil.getTokenKey());
    }

    public static boolean verifyToken(JSONObject json){
        if(!json.has("token"))
            return false;
        String token = json.getString("token");
        String uid = json.getString("uid");
        return userTokenMap.containsKey(uid) && token.equals(userTokenMap.get(uid));
    }

}
