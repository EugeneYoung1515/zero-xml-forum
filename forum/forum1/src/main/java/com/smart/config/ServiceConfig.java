package com.smart.config;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableTransactionManagement
@ComponentScan({"com.smart.service","com.smart.redisservice","com.smart.serviceinterfaces"})
@EnableCaching
public class ServiceConfig{

    @Autowired
    private DaoConfig daoConfig;

    //这个就把daoConfig里的hibernateTransactionManager给重写了
    @Bean
    public HibernateTransactionManager hibernateTransactionManager(){
        HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
        hibernateTransactionManager.setSessionFactory(daoConfig.sessionFactory());
        return hibernateTransactionManager;
    }

    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter(){
        System.out.println("adapter");
        return new RequestMappingHandlerAdapter();
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean(){
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));//这里不好还是用到了xml
        ehCacheManagerFactoryBean.setShared(true);
        return ehCacheManagerFactoryBean;
        //会不会少配置了EhCacheCacheManager这个bean

        //上面的 //@EnableCaching似乎有问题
    }

    @Bean
    public EhCacheCacheManager cacheManager(){
        System.out.println("ehcache");
        return new EhCacheCacheManager(ehCacheManagerFactoryBean().getObject());
    }


    /*
    @Bean
    public net.sf.ehcache.CacheManager ehCacheManager(){
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("default");
        cacheConfiguration.setMaxEntriesLocalHeap(10000);
        cacheConfiguration.setEternal(false);
        PersistenceConfiguration persistenceConfiguration = new PersistenceConfiguration();
        persistenceConfiguration.strategy(PersistenceConfiguration.Strategy.NONE);
        cacheConfiguration.persistence(persistenceConfiguration);
        cacheConfiguration.setTimeToIdleSeconds(0);
        cacheConfiguration.setTimeToLiveSeconds(0);
        cacheConfiguration.setDiskExpiryThreadIntervalSeconds(120);

        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(cacheConfiguration);

        CacheConfiguration cacheConfiguration1 = new CacheConfiguration();
        cacheConfiguration1.setName("fixedRegion");
        cacheConfiguration1.setMaxEntriesLocalHeap(100);
        cacheConfiguration1.setEternal(true);
        PersistenceConfiguration persistenceConfiguration1 = new PersistenceConfiguration();
        persistenceConfiguration1.strategy(PersistenceConfiguration.Strategy.NONE);
        cacheConfiguration1.persistence(persistenceConfiguration);
        config.addCache(cacheConfiguration1);

        CacheConfiguration cacheConfiguration2 = new CacheConfiguration();
        cacheConfiguration2.setName("freqChangeRegion");
        cacheConfiguration2.setMaxEntriesLocalHeap(5000);
        cacheConfiguration2.setEternal(false);
        PersistenceConfiguration persistenceConfiguration2 = new PersistenceConfiguration();
        persistenceConfiguration2.strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP);
        cacheConfiguration2.persistence(persistenceConfiguration);
        cacheConfiguration2.setTimeToIdleSeconds(300);
        cacheConfiguration2.setTimeToLiveSeconds(1800);
        config.addCache(cacheConfiguration2);

        config.getCacheConfigurations().forEach((k,v)->System.out.println(k));

        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Bean
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }
    //依赖中存在两个ehcache 上面的类 用新方法 不是过期的方法 运行起来时 会提示找不到方法
    */

}
