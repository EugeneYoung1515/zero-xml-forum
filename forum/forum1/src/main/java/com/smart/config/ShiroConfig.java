package com.smart.config;

import com.smart.redis.FastJson2JsonRedisSerializer;
import com.smart.service.UserService;
import com.smart.shiro.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.*;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;


@Configuration
//@ComponentScan("com.smart.shiro")
@ComponentScan("com.smart.web")
public class ShiroConfig {

    @Bean
    public MyShiroRealm myShiroRealm(){
        MyShiroRealm realm = new MyShiroRealm();
        realm.setCredentialsMatcher(hashedCredentialsMatcher());

        realm.setCachingEnabled(true);
        realm.setAuthenticationCachingEnabled(true);
        realm.setAuthorizationCachingEnabled(true);
        realm.setAuthenticationCacheName("authentication");
        realm.setAuthorizationCacheName("authorization");
        //这里使用使用这个名字无效 会变成默认名
        //把设置CacheManager放到下面就对了 原因是setCacheManager 内部会调用afterCacheManagerSet
        //AuthenticatingRealm this.authenticationCachingEnabled = false;
        //AuthorizingRealm this.authorizationCachingEnabled = true;
        //这两个的差异导致的

        realm.setCacheManager(redisCacheManager());

        System.out.println(realm.getAuthorizationCacheName());
        return realm;
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
    public TouchShiroFilterFactoryBean shiroFilterFactoryBean() {
        //shiroFilterFactoryBean用到securityManager
        //securityManager用到myShiroRealm和cookieRememberMeManager
        //cookieRememberMeManager用到rememberMeCookie

        TouchShiroFilterFactoryBean shiroFilterFactoryBean = new TouchShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());

        //验证码过滤器
        //Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
        //filterMap.put("authc", captchaFormAuthenticationFilter());
        //shiroFilterFactoryBean.setFilters(filterMap);

        Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
        //FormAuthenticationFilter formAuthenticationFilter = new FormAuthenticationFilter();
        CaptchaFormAuthenticationFilter formAuthenticationFilter=new CaptchaFormAuthenticationFilter();
        formAuthenticationFilter.setUsernameParam("userName");
        filterMap.put("authc", formAuthenticationFilter);
        shiroFilterFactoryBean.setFilters(filterMap);

        shiroFilterFactoryBean.setSuccessUrl("/index.html");
        shiroFilterFactoryBean.setLoginUrl("/login/login.html");

       //shiroFilterFactoryBean.setLoginUrl("/login.jsp");

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/index.html", "anon");
        filterChainDefinitionMap.put("/index.jsp", "anon");
        filterChainDefinitionMap.put("/login.jsp", "anon");
        filterChainDefinitionMap.put("/login/doLogout.html", "logout");
        filterChainDefinitionMap.put("/captcha.jpg", "anon");//图片验证码
        filterChainDefinitionMap.put("/register.jsp", "anon");
        filterChainDefinitionMap.put("/register.html", "anon");

        //filterChainDefinitionMap.put("/login/doLogin.html", "anon");

        filterChainDefinitionMap.put("/login/login.html", "authc");
        filterChainDefinitionMap.put("/**", "user");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /*
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

        Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
        //FormAuthenticationFilter formAuthenticationFilter = new FormAuthenticationFilter();
        CaptchaFormAuthenticationFilter formAuthenticationFilter=new CaptchaFormAuthenticationFilter();
        formAuthenticationFilter.setUsernameParam("userName");
        filterMap.put("authc", formAuthenticationFilter);
        shiroFilterFactoryBean.setFilters(filterMap);

        shiroFilterFactoryBean.setSuccessUrl("/index.html");
        shiroFilterFactoryBean.setLoginUrl("/login/login.html");

        //shiroFilterFactoryBean.setLoginUrl("/login.jsp");

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/index.html", "anon");
        filterChainDefinitionMap.put("/index.jsp", "anon");
        filterChainDefinitionMap.put("/login.jsp", "anon");
        filterChainDefinitionMap.put("/login/doLogout.html", "logout");
        filterChainDefinitionMap.put("/captcha.jpg", "anon");//图片验证码
        filterChainDefinitionMap.put("/register.jsp", "anon");
        filterChainDefinitionMap.put("/register.html", "anon");

        //filterChainDefinitionMap.put("/login/doLogin.html", "anon");

        filterChainDefinitionMap.put("/login/login.html", "authc");
        filterChainDefinitionMap.put("/**", "user");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }
    */
    /*
    要有一个url作为，来setLoginUrl，同时这个url的过滤器要设置为authc。
    web层里使用get方法访问这个url，返回的视图是登录页。
    shiro把请求转发到这个url，是get方法，拿到登录页。
    提交登录页，也就是登录表单提交的url，也要是这个url。
    用户名、密码和验证码，验证正确后，shiro重定向到之前的url。
    验证不通过，请求属性里就有shiro的异常名。就通过post方法，访问这个url。
    使用post方法，访问这个url，也可以直接返回登录页。登录显示shiro异常名。如果是这样处理的话，get方法和post方法都是放回登录页，控制器方法可以设置为

    @RequestMapping("/login")
	public String login(){
		return "login";
	}

    也可以post访问这个url时，在控制器方法中，把因为的异常名转化为中文的提示。

    shiro的涉及到的组件会比spring的组件提前初始化，realm依赖的service层对象，会提前初始化，就不能提前使用spring的切面带来的事务。
    上面的过滤器要设置属性，就不能单独作为一个bean，标上@Bean，而要在 shiroFilterFactoryBean中new对象。或者是在重写过滤器方法时，在方法中要调用setUsernameParam硬编码。
    好像也是这个原因。

    shiro使用切面进行权限控制时，也要做额外步骤，好像也是这个原因。

     */

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myShiroRealm());
        securityManager.setRememberMeManager(cookieRememberMeManager());

        securityManager.setSessionManager(sessionManager());
        //securityManager.setCacheManager(redisCacheManager());
        return securityManager;
    }

    //cookie对象;
    @Bean
    public SimpleCookie rememberMeCookie() {//能设置rememberMe cookies到浏览器中 但是好像不起作用 rememberMe cookies似乎要结合过滤器才能起作用
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

    /*
        @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }
上面两个放在了sprig mvc配置文件的最开头
     */


    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        hashedCredentialsMatcher.setHashIterations(3);
        return hashedCredentialsMatcher;
    }

    @Bean
    public SessionDAO redisSessionDAO(){
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        //redisSessionDAO.setJedisPool(new JedisPool(new JedisPoolConfig(),"localhost",6379,2000,"kmlbyz520"));
        //redisSessionDAO.setSerializer(new FastJson2JsonRedisSerializer<>(Session.class));
        //redisSessionDAO.setSerializerSimpleSession(new FastJson2JsonRedisSerializer<>(SimpleSession.class));

        RedisSessionDAOJedis redisSessionDAOJedis = new RedisSessionDAOJedis(new JedisPool(new JedisPoolConfig(),"localhost",6379,2000,"kmlbyz520"),
                new FastJson2JsonRedisSerializer<>(SimpleSession.class));

        redisSessionDAO.setRedisSessionDAOJedis(redisSessionDAOJedis);

        redisSessionDAO.setSessionIdGenerator(sessionIdGenerator());
        return redisSessionDAO;
    }

    /*
    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());

        sessionManager.setCacheManager(redisCacheManager());

        //sessionManager.setSessionFactory(sessionFactory());
        return sessionManager;
    }
    */

    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());

        sessionManager.setCacheManager(redisCacheManager());

        sessionManager.setSessionFactory(sessionFactory());

        //sessionManager.setGlobalSessionTimeout(120000);
        //sessionManager.setDeleteInvalidSessions(false);

        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdCookie(sessionIdCookie());
        return sessionManager;
    }

    @Bean
    public RedisCacheManager redisCacheManager(){
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setJedisPool(new JedisPool(new JedisPoolConfig(),"localhost",6379,2000,"kmlbyz520"));
        return redisCacheManager;
    }

    @Bean(name = "touchSessionFactory")
    public SessionFactory sessionFactory(){
        return new TouchSessionFactory();
    }

    @Bean
    public SessionValidationScheduler executorServiceSessionValidationScheduler(){
        ExecutorServiceSessionValidationScheduler executorServiceSessionValidationScheduler = new ExecutorServiceSessionValidationScheduler();

        DefaultWebSessionManager defaultWebSessionManager  = (DefaultWebSessionManager)sessionManager();
        executorServiceSessionValidationScheduler.setSessionManager(defaultWebSessionManager);
        defaultWebSessionManager.setSessionValidationScheduler(executorServiceSessionValidationScheduler);

        //executorServiceSessionValidationScheduler.setInterval(400000);
        return executorServiceSessionValidationScheduler;
    }

    @Bean
    public JavaUuidSessionIdGenerator sessionIdGenerator(){
        return new JavaUuidSessionIdGenerator();
    }

    @Bean
    public SimpleCookie sessionIdCookie(){
        SimpleCookie simpleCookie = new SimpleCookie("uid");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(-1);
        return simpleCookie;
    }
}