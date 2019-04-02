package controller;

import bean.user.UserBean;
import json.JSONObject;
import util.ContactUtil;
import util.SocketUtil;
import util.UserUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class ServerHandleThread implements Runnable {
    private Socket socket;
    private boolean serverOver = false;

    public ServerHandleThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (!serverOver) {
                String msg = SocketUtil.getMessage(socket);
                processMsg(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMsg(String msg) throws IOException {
        if ("hEaRtBEatS".equals(msg))
            return;
        if ("".equals(msg)) {
            onClose(null);
            return;
        }
        JSONObject json = new JSONObject(msg);
        if (json.has("login")) {
            processLogin(json);
        } else if (json.has("addContact")) {
            processAddContact(json);
        } else if (json.has("deleteContact")) {
            processDeleteContact(json);
        } else if (json.has("close")) {
            if (UserUtil.verifyToken(json)) {
                onClose(json);
            }
        }
    }

    private void processLogin(JSONObject json) throws IOException {
        String uid = json.getString("uid");
        String pwd = json.getString("pwd");
        UserBean user = UserUtil.login(uid, pwd);
        JSONObject respJson = new JSONObject();
        if (user == null) {
            respJson.put("state", -1);
        } else if ("".equals(user.getUserId())) {
            respJson.put("state", -2);
        } else {
            respJson.put("state", 1);
            respJson.put("name", user.getUserName());
            String token = UserUtil.getUserToken(uid, pwd);
            respJson.put("token", token);
            respJson.put("contactList", ContactUtil.getContactsList(uid));
            UserUtil.userTokenMap.put(uid, token);
        }
        SocketUtil.sendMessage(socket, respJson.toString());
    }

    private void processAddContact(JSONObject json) throws IOException {
        if (!UserUtil.verifyToken(json))
            return;
        String sourceId = json.getString("uid");
        String contactId = json.getString("contactId");
        JSONObject respJson = ContactUtil.addContact(sourceId, contactId);
        SocketUtil.sendMessage(socket, respJson.toString());
        Map<String, UserBean> contactMap = SocketUtil.contactMapOfUser.get(sourceId);
        UserBean contact = new UserBean();
        contact.setUserId(contactId);
        if (contactMap != null)
            contactMap.put(contactId, contact);
    }

    private void processDeleteContact(JSONObject json) throws IOException {
        if (!UserUtil.verifyToken(json))
            return;
        String sourceId = json.getString("uid");
        String contactId = json.getString("contactId");
        JSONObject respJson = ContactUtil.deleteContact(sourceId, contactId);
        SocketUtil.sendMessage(socket, respJson.toString());
        Map<String, UserBean> contactMap = SocketUtil.contactMapOfUser.get(contactId);
        if (contactMap != null)
            contactMap.remove(sourceId);
    }

    private void onClose(JSONObject json) throws IOException {
        serverOver = true;
        if (json != null)
            UserUtil.userTokenMap.remove(json.getString("uid"));
        socket.getInputStream().close();
        socket.close();
    }

}

