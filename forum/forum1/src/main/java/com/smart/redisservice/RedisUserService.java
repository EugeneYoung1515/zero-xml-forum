package com.smart.redisservice;

import com.smart.domain.LoginLog;
import com.smart.domain.User;
import com.smart.exception.UserExistException;
import com.smart.redisdao.LoginLogDao;
import com.smart.redisdao.UserDao;
import com.smart.serviceinterfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("redisUserService")
public class RedisUserService implements UserServiceInterface {

    private com.smart.redisdao.UserDao redisUserDao;
    private com.smart.redisdao.LoginLogDao redisLoginLogDao;

    @Override
    public void register(User user) throws UserExistException {
        User u = this.getUserByUserName(user.getUserName());
        if(u != null){
            throw new UserExistException("用户名已经存在");
        }else{
            user.setCredit(100);
            user.setUserType(2);
            redisUserDao.save(user);
        }
    }

    @Override
    public void update(User user) {
        redisUserDao.update(user);
    }

    @Override
    public User getUserByUserName(String userName) {
        return redisUserDao.getUserByUserName(userName);
    }

    @Override
    public List<User> getAllUsers() {
        return redisUserDao.loadAll();
    }

    @Override
    public void loginSuccess(User user) {
        user.setCredit( 5 + user.getCredit());

        LoginLog loginLog = new LoginLog();
        loginLog.setUser(user);
        loginLog.setIp(user.getLastIp());
        loginLog.setLoginDate(new Date());

        redisUserDao.update(user);
        redisLoginLogDao.save(loginLog);//一天登录一次加5分比较好
    }

    @Autowired
    @Qualifier("redisUserDao")
    public void setRedisUserDao(UserDao redisUserDao) {
        this.redisUserDao = redisUserDao;
    }

    @Autowired
    @Qualifier("redisLoginLogDao")
    public void setRedisLoginLogDao(LoginLogDao redisLoginLogDao) {
        this.redisLoginLogDao = redisLoginLogDao;
    }
}
