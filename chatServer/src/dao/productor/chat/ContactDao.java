package dao.productor.chat;

import bean.user.UserBean;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/**抽象产品*/

public interface ContactDao {
    UserBean addContact(String sourceId, String contactId);

    boolean deleteContact(String sourceId, String targetId);

    Map<String, UserBean> getContactsMap(String userId);

    void updateLatestContactTime(String contactId, Map<String, Timestamp> latestContactTimeMap);
}
