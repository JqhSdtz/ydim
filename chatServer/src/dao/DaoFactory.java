package dao;

import dao.productor.chat.ContactDao;
import dao.productor.chat.MessageDao;
import dao.productor.user.UserDao;

/**创建DAO的抽象工厂，UserDao, ContactDao, MessageDao是位于不同产品等级结构中的一组产品，即产品族，都是接口类
 * UserDaoJDBCImpl, ContactDaoJDBCImpl, MessageDaoJDBCImpl是产品族的JDBC实现，是实体类**/

public interface DaoFactory {
    UserDao createUserDao();

    ContactDao createContactDao();

    MessageDao createMessageDao();
}
