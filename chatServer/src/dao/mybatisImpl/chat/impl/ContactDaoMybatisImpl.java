package dao.mybatisImpl.chat.impl;

import bean.chat.ContactRelShipBean;
import bean.user.UserBean;
import dao.mybatisImpl.ContactsMapResultHandler;
import dao.mybatisImpl.chat.mapper.ContactMapper;
import dao.mybatisImpl.user.mapper.UserMapper;
import dao.productor.chat.ContactDao;
import org.apache.ibatis.session.SqlSession;
import util.MybatisUtil;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

public class ContactDaoMybatisImpl implements ContactDao {
    @Override
    public UserBean addContact(String sourceId, String contactId){
        SqlSession session = MybatisUtil.getSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        ContactMapper contactMapper = session.getMapper(ContactMapper.class);
        UserBean user;
        try {
            user = userMapper.selectUserById(contactId);
            user.setLatestContactTime(new Timestamp(new Date().getTime()));
            //若目标用户存在
            if(isContact(contactMapper, sourceId, contactId)){//曾经添加过联系人
                if(ContactRelShipBean.compareUid(sourceId, contactId))
                    contactMapper.converseRelValueById(sourceId, contactId);
                else
                    contactMapper.converseRelValueById(contactId, sourceId);
            }
            else{//没有添加过联系人
                /*添加联系人*/
                ContactRelShipBean relship;
                if(ContactRelShipBean.compareUid(sourceId, contactId))
                    relship = new ContactRelShipBean(sourceId, contactId,
                            new Timestamp(user.getLatestContactTime().getTime()));
                else
                    relship = new ContactRelShipBean(contactId, sourceId,
                            new Timestamp(user.getLatestContactTime().getTime()));
                contactMapper.insertContactrelship(relship);
            }
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
            user = null;
        } finally {
            session.close();
        }
        return user;
    }

    @Override
    public boolean deleteContact(String sourceId, String targetId){
        boolean succeed = false;
        SqlSession session = MybatisUtil.getSession();
        ContactMapper contactMapper = session.getMapper(ContactMapper.class);
        try {//删除好友即将好友的关系值取负值
            if(ContactRelShipBean.compareUid(sourceId, targetId))
                contactMapper.converseRelValueById(sourceId, targetId);
            else
                contactMapper.converseRelValueById(targetId, sourceId);
            session.commit();
            succeed = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return succeed;
    }


    @Override
    public Map<String, UserBean> getContactsMap(String userId) {
        SqlSession session = MybatisUtil.getSession();
        ContactsMapResultHandler resultHandler = new ContactsMapResultHandler();
        session.select("selectContactsMapById", userId, resultHandler);
        @SuppressWarnings("rawtypes")
        Map contactsMap =resultHandler.getMappedResults();
        session.close();
        return contactsMap;
    }


    @Override
    public void updateLatestContactTime(String contactId, Map<String, Timestamp> latestContactTimeMap){
        SqlSession session = MybatisUtil.getSession();
        ContactMapper contactMapper = session.getMapper(ContactMapper.class);
        try {
            ContactRelShipBean relship = new ContactRelShipBean();
            for (Map.Entry<String, Timestamp> entry : latestContactTimeMap.entrySet()){
                relship.setLatestContactTime(new Timestamp(entry.getValue().getTime()));
                if(ContactRelShipBean.compareUid(contactId, entry.getKey())){
                    relship.setUid1(contactId);
                    relship.setUid2(entry.getKey());
                }else{
                    relship.setUid2(contactId);
                    relship.setUid1(entry.getKey());
                }
                contactMapper.updateLatestContactTime(relship);
                session.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    private boolean isContact(ContactMapper contactMapper, String uid1, String uid2){
        boolean flag = true;
        if(ContactRelShipBean.compareUid(uid1, uid2)){
            if(contactMapper.selectRelShipById(uid1, uid2) == null)
                flag = false;
        }else{
            if(contactMapper.selectRelShipById(uid2, uid1) == null)
                flag = false;
        }
        return flag;
    }

}
