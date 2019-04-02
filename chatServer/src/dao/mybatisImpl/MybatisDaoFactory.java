package dao.mybatisImpl;

import dao.DaoFactory;
import dao.mybatisImpl.chat.impl.ContactDaoMybatisImpl;
import dao.mybatisImpl.chat.impl.MessageDaoMybatisImpl;
import dao.mybatisImpl.user.impl.UserDaoMybatisImpl;
import dao.productor.chat.ContactDao;
import dao.productor.chat.MessageDao;
import dao.productor.user.UserDao;

public class MybatisDaoFactory implements DaoFactory {
    public UserDao createUserDao() { return new UserDaoMybatisImpl(); }

    public ContactDao createContactDao() { return new ContactDaoMybatisImpl(); }

    public MessageDao createMessageDao() { return new MessageDaoMybatisImpl(); }

}
