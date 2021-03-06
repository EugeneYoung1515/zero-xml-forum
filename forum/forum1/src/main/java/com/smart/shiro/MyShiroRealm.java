package com.smart.shiro;

import com.smart.domain.Board;
import com.smart.domain.User;
import com.smart.service.UserService;
import com.smart.serviceinterfaces.UserServiceInterface;
import com.smart.utils.RequestHolderUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyShiroRealm extends AuthorizingRealm {

    @Lazy
    @Autowired
    @Qualifier("redisUserService")
    private UserServiceInterface userService;
    /*
    00:28:54 INFO  Bean 'com.smart.config.ShiroConfig' of type [class com.smart.config.ShiroConfig$$EnhancerBySpringCGLIB$$572e61df] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
00:28:54 INFO  Bean 'org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration' of type [class org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration$$EnhancerBySpringCGLIB$$8ed90841] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
00:28:54 INFO  Bean 'myShiroRealm' of type [class com.smart.shiro.MyShiroRealm] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
00:28:54 INFO  Bean 'rememberMeCookie' of type [class org.apache.shiro.web.servlet.SimpleCookie] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
00:28:54 INFO  Bean 'cookieRememberMeManager' of type [class org.apache.shiro.web.mgt.CookieRememberMeManager] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
00:28:54 INFO  Bean 'securityManager' of type [class org.apache.shiro.web.mgt.DefaultWebSecurityManager] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)

使用了@Lazy注解后 还是有这几个类不能被织入切面
和自己的那个个Spring Boot Mybatis Shiro一样
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        //CaptchaUsernamePasswordToken captchaUsernamePasswordToken = (CaptchaUsernamePasswordToken)token;

        String username = (String) token.getPrincipal();
        User dbUser = userService.getUserByUserName(username);

        System.out.println("here");

        /*
        User dbUser=new User();
        dbUser.setUserName("qwer");
        dbUser.setPassword("123456");
        */
        //硬编码用于测试

        if (dbUser == null) {
            //没有返回登录用户名对应的SimpleAuthenticationInfo对象时,就会在LoginController中抛出UnknownAccountException异常
            return null;
        }

        if(dbUser.getLocked()==User.USER_LOCK){
            throw new LockedAccountException();
        }

        //验证通过返回一个封装了用户信息的AuthenticationInfo实例即可。
        /*
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                dbUser, //用户信息
                dbUser.getPassword(), //密码
                getName() //realm name
        );//换成了加盐的
        */
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                dbUser, //用户信息
                dbUser.getPassword(), //密码
                //ByteSource.Util.bytes(username),//盐
                new SerializableByteSource(username),
                getName() //realm name
        );//换成了加盐的
        return authenticationInfo;
    }

    //当访问到页面的时候，链接配置了相应的权限或者shiro标签才会执行此方法否则不会执行
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals){
        //return null;

        System.out.println("doGetAuthorizationInfo");
        User user = (User) principals.fromRealm(getName()).iterator().next();
        if(user!=null){
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            List<String> permsList = new ArrayList<>(10);
            if(user.getUserType()==2){
                permsList.add("sys:man");
                info.addStringPermissions(permsList);
            }else{
                HttpServletRequest request = RequestHolderUtil.getRequest();
                String contextRelativePath = request.getRequestURI().substring(request.getContextPath().length());
                System.out.println(contextRelativePath);
                if (contextRelativePath.contains("board")) {
                    int boardId = Integer.parseInt(contextRelativePath.split("/")[2]);
                    Set<Board> boardSet = user.getManBoards();
                    boolean flag = false;
                    for (Board board : boardSet) {
                        if (board.getBoardId() == boardId) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        permsList.add("board:man");
                    }
                    info.addStringPermissions(permsList);
                }else{
                    return null;//或者不加权限 空list行吗
                }
            }
            return info;
        }
        return null;
    }

    public void setUserService(UserServiceInterface userService) {
        this.userService = userService;
    }
}

