package dao.mybatisImpl.user.impl;

import bean.user.UserBean;
import bean.user.UserInfoBean;
import dao.mybatisImpl.user.mapper.UserMapper;
import dao.productor.user.UserDao;
import org.apache.ibatis.session.SqlSession;
import util.MybatisUtil;

public class UserDaoMybatisImpl implements UserDao {

    @Override
    public boolean createUser(UserBean user) {
        boolean succeed = false;
        SqlSession session = MybatisUtil.getSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        if (userMapper.selectUserById(user.getUserId()) == null) {
            userMapper.insertUser(user);
            session.commit();
            succeed = true;
        }
        session.close();
        return succeed;
    }

    @Override
    public UserBean login(String userId, String password) {
        SqlSession session = MybatisUtil.getSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        UserBean user = userMapper.selectUserById(userId);
        if (user != null) {
            String pwd = user.getPassword();
            if (!password.equals(pwd)) {//密码错误
                user.setPassword("");
                user.setUserId(userId);
            }
        } else {//帐号不存在
            user = new UserBean();
            user.setUserId("");
            user.setPassword(password);
        }
        session.close();
        return user;
    }

    @Override
    public boolean checkUserId(String userId) {
        boolean existed = false;
        SqlSession session = MybatisUtil.getSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        UserBean user;
        user = userMapper.selectUserById(userId);
        if (user != null)
            existed = true;
        session.close();
        return existed;
    }

    @Override
    public boolean passwordVerify(String userId, String password) {
        boolean valid = false;
        SqlSession session = MybatisUtil.getSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        UserBean user = userMapper.selectUserById(userId);
        if(user != null && user.getPassword().equals(password))
            valid = true;
        return valid;
    }

    @Override
    public UserBean getUserById(String userId) {
        SqlSession session = MybatisUtil.getSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        UserBean user = null;
        user = userMapper.selectUserById(userId);
        session.close();
        return user;
    }

    @Override
    public void modifyUserName(String userId, String userName) {
        SqlSession session = MybatisUtil.getSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        userMapper.updateUserNameById(userId, userName);
        session.commit();
        session.close();
    }

    @Override
    public void modifyPassword(String userId, String password) {
        SqlSession session = MybatisUtil.getSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        userMapper.updatePasswordById(userId, password, UserBean.getPwdKey());
        session.commit();
        session.close();
    }


    @Override
    public UserInfoBean getUserInfo(String userId) {
        SqlSession session = MybatisUtil.getSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        UserInfoBean userInfo = userMapper.selectUserInfoById(userId);
        session.close();
        return userInfo;
    }
}
