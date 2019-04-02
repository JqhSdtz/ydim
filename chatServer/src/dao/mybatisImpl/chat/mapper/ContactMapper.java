package dao.mybatisImpl.chat.mapper;

import bean.chat.ContactRelShipBean;
import bean.user.UserBean;
import org.apache.ibatis.annotations.Param;

import java.io.IOException;
import java.util.Map;

public interface ContactMapper {
    void insertContactrelship(ContactRelShipBean contactRelShip) throws IOException;

    Object selectRelShipById(@Param("uid1") String uid1, @Param("uid2") String uid2);

    void converseRelValueById(@Param("uid1") String uid1, @Param("uid2") String uid2) throws IOException;

    Map<String, UserBean> selectContactsMapById(String contactId);

    void updateLatestContactTime(ContactRelShipBean contactRelShip) throws IOException;

}
