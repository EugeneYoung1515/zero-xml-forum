package com.smart.config;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
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
//@EnableCaching
public class ServiceConfig {

    /*
    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter(){
        return new RequestMappingHandlerAdapter();
    }
    加上这个 就会

    Overriding bean definition for bean 'requestMappingHandlerAdapter' with a different definition: replacing
    [Root bean: class [null]; scope=; abstract=false; lazyInit=false; autowireMode=3; dependencyCheck=0;
    autowireCandidate=true; primary=false; factoryBeanName=serviceConfig; factoryMethodName=requestMappingHandlerAdapter;
    initMethodName=null; destroyMethodName=(inferred); defined
    in class path resource [com/smart/config/ServiceConfig.class]]
    with
    [Root bean: class [null]; scope=; abstract=false; lazyInit=false; autowireMode=3; dependencyCheck=0;
    autowireCandidate=true; primary=false; factoryBeanName=org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration$EnableWebMvcConfiguration;
    factoryMethodName=requestMappingHandlerAdapter; initMethodName=null; destroyMethodName=(inferred); defined
    in class path resource [org/springframework/boot/autoconfigure/web/WebMvcAutoConfiguration$EnableWebMvcConfiguration.class]]

    */
}
