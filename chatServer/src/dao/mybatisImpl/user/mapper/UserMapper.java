package dao.mybatisImpl.user.mapper;

import bean.user.UserBean;
import bean.user.UserInfoBean;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    void insertUser(@Param("user") UserBean user);
    UserBean selectUserById(@Param("userId") String userId);
    void updateUserNameById(@Param("userId") String userId, @Param("userName") String userName);
    void updatePasswordById(@Param("userId") String userId, @Param("password") String password,
                            @Param("pwd_key") String password_key);
    UserInfoBean selectUserInfoById(String userId);
}
