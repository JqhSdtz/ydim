package dao.productor.user;

import bean.user.UserBean;
import bean.user.UserInfoBean;

/**抽象产品*/

public interface UserDao {
    //创建成功返回true，否则返回false
    boolean createUser(UserBean user);

    // 登录成功返回用户的UserBean,帐号不存在则UserBean的userId为空，若密码错误则UserBean的password为空
    //若产生异常则返回null
    UserBean login(String userId, String password);

    boolean checkUserId(String userId);

    boolean passwordVerify(String userId, String password);

    UserBean getUserById(String userId);

    void modifyUserName(String userId, String userName);

    void modifyPassword(String userId, String password);

    UserInfoBean getUserInfo(String userId);

}
