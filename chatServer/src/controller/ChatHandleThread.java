package controller;

import bean.chat.MessageBean;
import bean.user.UserBean;
import dao.mybatisImpl.MybatisDaoFactory;
import dao.productor.chat.ContactDao;
import dao.productor.chat.MessageDao;
import json.JSONArray;
import json.JSONObject;
import util.SocketUtil;
import util.UserUtil;

import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.*;

public class ChatHandleThread implements Runnable {
    private Socket socket;
    private Map<String , Timestamp> latestContactTimeMap = new HashMap<>();
    private UserBean clientUser = new UserBean();
    private Map<String, UserBean> clientContactsMap;
    private boolean chatOver = false;

    public ChatHandleThread(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run(){
        try {
            while (!chatOver) {
                String msg = SocketUtil.getMessage(socket);
                processMsg(msg);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMsg(String msg){
        if("hEaRtBEatS".equals(msg))
            return;
        if("".equals(msg)){
            onClose();
            return;
        }
        JSONObject json = new JSONObject(msg);
        if(!UserUtil.verifyToken(json))
            return;
        if(json.has("open"))
            onOpen(json);
        else if(json.has("close"))
            onClose();
        else if(json.has("chat"))
            onMessage(json);
    }

    private void onOpen(JSONObject json){
        String uid = json.getString("uid");
        clientUser.setUserId(uid);
        getContactsMap(uid);
        SocketUtil.socketMap.put(uid, socket);
        JSONObject online = new JSONObject();
        online.put("onlineNotify", true);
        online.put("uid", uid);
        sendMessageToContactList(online.toString());
        if(SocketUtil.offlineMessageMap.containsKey(clientUser.getUserId())){
            try{
                sendOfflineMessageToClient();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        SocketUtil.onlineCount.incrementAndGet();
    }

    private void onClose(){
        SocketUtil.socketMap.remove(clientUser.getUserId());
        SocketUtil.onlineCount.decrementAndGet();
        JSONObject offline = new JSONObject();
        offline.put("offlineNotify", true);
        offline.put("uid", this.clientUser.getUserId());
        sendMessageToContactList(offline.toString());
        updateLatestContactTime();
        latestContactTimeMap.clear();
        chatOver = true;
        SocketUtil.contactMapOfUser.remove(clientUser.getUserId());
        try{
            //socket.getInputStream().close();
            SocketUtil.sendMessage(socket, "");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void onMessage(JSONObject json){
        try {
            if(json.has("updateContactTime"))
                latestContactTimeMap.put(json.getString("targetUserId"), new Timestamp(new Date().getTime()));
            else {
                json.put("sourceUserId", clientUser.getUserId());
                sendChatMessage(json, json.toString());
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void getContactsMap(String userId){
        ContactDao contactDao = new MybatisDaoFactory().createContactDao();
        clientContactsMap = contactDao.getContactsMap(userId);
        SocketUtil.contactMapOfUser.put(userId, clientContactsMap);
    }

    private void sendChatMessage(JSONObject json, String message) throws IOException {
        String targetUserId = json.getString("targetUserId");
        if (SocketUtil.socketMap.containsKey(targetUserId)) {//目标用户在线
            SocketUtil.sendMessage(SocketUtil.socketMap.get(targetUserId), message);
        } else {//目标用户离线
            saveOfflineMessage(json);
            JSONObject offline = new JSONObject();
            offline.put("offline", true);
            offline.put("uid", targetUserId);
            SocketUtil.sendMessage(socket, offline.toString());
        }
    }

    private void saveOfflineMessage(JSONObject json){
        MessageDao dao = new MybatisDaoFactory().createMessageDao();
        String contactId = json.getString("targetUserId");
        Date time = new Date();//json中的time是为了在客户端显示，数据库表中的time值是为了排序
        json.put("time",time);//离线消息都加上时间信息
        int messageSeq;
        if(SocketUtil.offlineMessageMap.containsKey(contactId)){
            messageSeq = SocketUtil.offlineMessageMap.get(contactId);
            ++messageSeq;
            SocketUtil.offlineMessageMap.put(contactId, messageSeq);
        }else{
            messageSeq = (int)dao.getMaxMessageSeq(contactId) + 1;
            SocketUtil.offlineMessageMap.put(contactId, messageSeq);
        }
        dao.saveOfflineMessage(new MessageBean(contactId, messageSeq, json.toString()));
    }

    private void sendMessageToContactList(String message){
        Map<String, UserBean> contactsMap = clientContactsMap;
        for(String contactId: contactsMap.keySet()){
            if (SocketUtil.socketMap.containsKey(contactId)) {
                try{
                    if(!contactId.equals(clientUser.getUserId()))
                        SocketUtil.sendMessage(SocketUtil.socketMap.get(contactId), message);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateLatestContactTime(){
        ContactDao dao = new MybatisDaoFactory().createContactDao();
        dao.updateLatestContactTime(clientUser.getUserId(), latestContactTimeMap);
    }

    private void sendOfflineMessageToClient() throws IOException{
        MessageDao dao = new MybatisDaoFactory().createMessageDao();
        List<MessageBean> messageList = dao.getOfflineMessageList(clientUser.getUserId());
        messageList.sort(Comparator.comparingInt(MessageBean::getMessageSeq));
        JSONArray messageArray = new JSONArray();
        for(MessageBean message: messageList){
            messageArray.put(new JSONObject(message.getMessage()));
        }
        JSONObject json = new JSONObject();
        json.put("offlineMessageArray", true);
        json.put("messageArray", messageArray);
        SocketUtil.sendMessage(socket, json.toString());
        messageList.clear();
        SocketUtil.offlineMessageMap.remove(clientUser.getUserId());
    }
}
