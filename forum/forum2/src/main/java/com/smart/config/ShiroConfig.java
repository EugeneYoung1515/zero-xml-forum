package com.smart.config;

import com.smart.shiro.MyShiroRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;


@Configuration
public class ShiroConfig {

    @Bean
    public MyShiroRealm myShiroRealm(){
        return new MyShiroRealm();
    }

    /*
    @Bean
    public CaptchaFormAuthenticationFilter captchaFormAuthenticationFilter(){
        return new CaptchaFormAuthenticationFilter();
    }
    */


    /*
    public FormAuthenticationFilter formAuthenticationFilter(){
        FormAuthenticationFilter formAuthenticationFilter = new FormAuthenticationFilter();
        formAuthenticationFilter.setUsernameParam("userName");
        return formAuthenticationFilter;
    }
    */

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        //shiroFilterFactoryBean用到securityManager
        //securityManager用到myShiroRealm和cookieRememberMeManager
        //cookieRememberMeManager用到rememberMeCookie

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());

        //验证码过滤器
        //Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
        //filterMap.put("authc", captchaFormAuthenticationFilter());
        //shiroFilterFactoryBean.setFilters(filterMap);

        //Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
        //FormAuthenticationFilter formAuthenticationFilter = new FormAuthenticationFilter();
        //formAuthenticationFilter.setUsernameParam("userName");
        //filterMap.put("authc", formAuthenticationFilter);
        //shiroFilterFactoryBean.setFilters(filterMap);

        shiroFilterFactoryBean.setLoginUrl("/login.jsp");
        //shiroFilterFactoryBean.setSuccessUrl("/index.html");

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/index.html", "anon");
        filterChainDefinitionMap.put("/index.jsp", "anon");
        filterChainDefinitionMap.put("/login.jsp", "anon");
        filterChainDefinitionMap.put("/login/doLogout.html", "logout");
        filterChainDefinitionMap.put("/captcha.jpg", "anon");//图片验证码
        filterChainDefinitionMap.put("/register.jsp", "anon");
        filterChainDefinitionMap.put("/register.html", "anon");
        //filterChainDefinitionMap.put("/login/doLogin.html", "authc");//这个设置是有用的 但是设置后又跳到了login
        filterChainDefinitionMap.put("/login/doLogin.html", "anon");

        //filterChainDefinitionMap.put("/**","user");//能起作用
        filterChainDefinitionMap.put("/**","authc");//能起作用

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myShiroRealm());
        securityManager.setRememberMeManager(cookieRememberMeManager());
        return securityManager;
    }
/*
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }//作用是？
*/

    //cookie对象;
    @Bean
    public SimpleCookie rememberMeCookie() {//能设置rememberMe cookies到浏览器中
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");

        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
        simpleCookie.setMaxAge(2592000);
        return simpleCookie;
    }

    //cookie管理对象;
    @Bean
    public CookieRememberMeManager cookieRememberMeManager() {
        CookieRememberMeManager manager = new CookieRememberMeManager();
        manager.setCookie(rememberMeCookie());
        return manager;
    }
     @Bean
     public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
         return new LifecycleBeanPostProcessor();
     }//这个一定要吗
        //自己不使用shiro的注解
        //不会用到切面
        //还需要用到这个吗
}