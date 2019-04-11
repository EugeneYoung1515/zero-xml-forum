package com.smart.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.Properties;

@Configuration
@PropertySource("classpath:jdbc.properties")
@EnableTransactionManagement
@ComponentScan("com.smart.dao")//还是要把配置类拆成几个 因为单元测试时要用
public class DaoConfig {

    @Autowired
    private Environment environment;

    @Bean(name = "dataSource",destroyMethod = "close")
    public BasicDataSource basicDataSource(){
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(environment.getProperty("jdbc.driverClassName"));
        basicDataSource.setUrl(environment.getProperty("jdbc.url"));
        basicDataSource.setUsername(environment.getProperty("jdbc.username"));
        basicDataSource.setPassword(environment.getProperty("jdbc.password"));
        return basicDataSource;
    }

    @Bean
    public LocalSessionFactoryBean localSessionFactoryBean(){
        LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
        localSessionFactoryBean.setDataSource(basicDataSource());
        localSessionFactoryBean.setPackagesToScan("com.smart.domain");
        Properties properties = new Properties();
        properties.put("hibernate.dialect",environment.getProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql",environment.getProperty("hibernate.show_sql"));
        properties.put("hibernate.cache.use_second_level_cache",environment.getProperty("hibernate.cache.use_second_level_cache"));
        properties.put("hibernate.cache.region.factory_class",environment.getProperty("hibernate.cache.region.factory_class"));
        properties.put("hibernate.cache.use_query_cache",environment.getProperty("hibernate.cache.use_query_cache"));
        properties.put("hibernate.generate_statistics","true");
        localSessionFactoryBean.setHibernateProperties(properties);//这块特傻
        //这里应该能换成读入一个属性文件 而不是构建一个属性文件再从一个属性文件中读入这个属性文件
        return localSessionFactoryBean;
    }

    @Bean
    public SessionFactory sessionFactory(){
        return localSessionFactoryBean().getObject();
    }

    @Bean
    public HibernateTemplate hibernateTemplate(){
        HibernateTemplate hibernateTemplate = new HibernateTemplate();
        hibernateTemplate.setSessionFactory(sessionFactory());
        return hibernateTemplate;
    }

    @Bean
    public HibernateTransactionManager hibernateTransactionManager(){
        HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
        hibernateTransactionManager.setSessionFactory(sessionFactory());
        return hibernateTransactionManager;
    }//这边切点方法还没设置

    //使用xml 那边有同名的事务管理器bean 同名的bean如果在不同的配置文件中会覆盖掉
    //但是tx advice 也同名了
    //然后dao层配置文件切点是所有层的所有方法
    //dao层 aop:advisor有 order等于1
    //service层的aop:advisor没有order 然后service层配置文件切点是service层所有方法
    //现在是同名tx advice的问题 还有没把service层配置文件中的切点转为@transactional注解
    //service层配置文件这个
    //bean id="requestMappingHandlerAdapter" class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter">
    //这个还没处理
}
