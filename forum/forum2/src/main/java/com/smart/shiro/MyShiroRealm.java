package com.smart.shiro;

import com.smart.domain.User;
import com.smart.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

public class MyShiroRealm extends AuthorizingRealm {

    @Lazy
    @Autowired
    private UserService userService;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //CaptchaUsernamePasswordToken captchaUsernamePasswordToken = (CaptchaUsernamePasswordToken)token;
        String username = (String) token.getPrincipal();
        User dbUser = userService.getUserByUserName(username);
        if (dbUser == null) {
            //没有返回登录用户名对应的SimpleAuthenticationInfo对象时,就会在LoginController中抛出UnknownAccountException异常
            return null;
        }

        if(dbUser.getLocked()==User.USER_LOCK){
            throw new LockedAccountException();
        }

        //验证通过返回一个封装了用户信息的AuthenticationInfo实例即可。
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                dbUser, //用户信息
                dbUser.getPassword(), //密码
                getName() //realm name
        );
        return authenticationInfo;
    }

    //当访问到页面的时候，链接配置了相应的权限或者shiro标签才会执行此方法否则不会执行
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals){return null;}

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    public UserService getUserService(){
        return userService;
    }
}

