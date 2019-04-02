package util;

import bean.user.UserBean;
import dao.mybatisImpl.MybatisDaoFactory;
import dao.productor.chat.ContactDao;
import json.JSONArray;
import json.JSONObject;

import java.util.Map;
import java.util.regex.Pattern;

public class ContactUtil {

    public static JSONObject addContact(String sourceId, String contactId){
        String idPattern = UserBean.getIdPattern();
        JSONObject json = new JSONObject();
        if (!Pattern.matches(idPattern, sourceId) || !Pattern.matches(idPattern, contactId)) {
            json.put("IdIllegal", true);
            return json;
        }
        ContactDao dao = new MybatisDaoFactory().createContactDao();
        UserBean contact = dao.addContact(sourceId, contactId);
        if (contact == null) {
            json.put("Exception", true);
        } else if ("".equals(contact.getUserId())) {
            json.put("IdNotFound", true);
        } else {
            json = createContactJSON(contact);
            json.put("Succeed",true);
        }
        return json;
    }

    public static JSONObject deleteContact(String source, String target){
        ContactDao dao = new MybatisDaoFactory().createContactDao();
        JSONObject json = new JSONObject();
        if(dao.deleteContact(source, target))
            json.put("Succeed", true);
        else
            json.put("IdIllegal", true);
        return json;
    }


    public static JSONArray getContactsList(String userId) {
        JSONArray jsonArray = new JSONArray();
        ContactDao contactDao = new MybatisDaoFactory().createContactDao();
        Map<String, UserBean> clientContactsMap = contactDao.getContactsMap(userId);
        for (Map.Entry<String, UserBean> contact: clientContactsMap.entrySet()) {
            jsonArray.put(createContactJSON(contact.getValue()));
        }
        return jsonArray;
    }

    private static JSONObject createContactJSON(UserBean contact){//根据UserBean对象创建JSON对象
        JSONObject contactJson = new JSONObject();
        contactJson.put("userId", contact.getUserId());
        contactJson.put("latestContactTime", contact.getLatestContactTime().getTime());//!!!
        contactJson.put("userName", contact.getUserName());
        if (UserUtil.userTokenMap.containsKey(contact.getUserId()))//若webSocketMap中有该用户的ID，则该用户在线
            contactJson.put("state", "在线");
        else
            contactJson.put("state", "离线");
        return contactJson;
    }
}
