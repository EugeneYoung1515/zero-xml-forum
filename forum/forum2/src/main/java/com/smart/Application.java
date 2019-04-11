package com.smart;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;


//@Configuration
//@ComponentScan
//@EnableAutoConfiguration

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
//@SpringBootApplication注解就相当于有@ComponentScan注解
//且不用加扫描的包
//能扫描到dao层的包中的bean
//而config包和dao包是同一层次也应该能扫描到

/*
springboot的自动配置

使用spring xml配置
容器按照bean.xml产生bean
按照bean.xml的配置的信息通过setter方法给bean注入依赖

使用spring javaconfig配置
配置类一般命名为*Configuration
并加上类注解@Configuration
配置类本身是个bean
配置的方法可以加上方法级注解@Bean
产生一个一个bean
在这个方法中手动加上调用setter方法注入依赖
@Bean注解的方法可以从.properties属性文件中读入配置给bean的实例变量赋值

使用springboot 自动配置
自动配置 产生自动配置类(命名为*AutoConfiguration，是个bean)bean
按照某种条件产生bean(使用了spring的条件注解)
自动配置类中的方法注解@Bean
产生bean bean的实例变量有默认值
如果application.proerties中有值 就会注入 覆盖默认值

也就是springboot自动配置
相当于自动化的javaconfig配置
因为使用了spring的条件注解

上面三种都要有先有bean的java类定义

*/


@EnableTransactionManagement
@SpringBootApplication
public class Application  extends SpringBootServletInitializer implements WebApplicationInitializer {


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    /*
    @Bean
    public FilterRegistrationBean shiroFilter(){
        FilterRegistrationBean shiroFilter = new FilterRegistrationBean();
        shiroFilter.setFilter(new DelegatingFilterProxy());
        shiroFilter.setName("shiroFilter");
        shiroFilter.addInitParameter("targetFilterLifecycle","true");
        shiroFilter.addUrlPatterns("/*");
        return shiroFilter;
    }
    */
    @Bean
    public FilterRegistrationBean delegatingFilterProxy(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shiroFilter");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean encodingFilter(){
        FilterRegistrationBean encodingFilter = new FilterRegistrationBean();
        encodingFilter.setFilter(new CharacterEncodingFilter());
        encodingFilter.setName("encodingFilter");
        encodingFilter.addInitParameter("encoding","UTF-8");
        encodingFilter.addInitParameter("forceEncoding","true");
        encodingFilter.addUrlPatterns("/*");
        return encodingFilter;
    }

    /*
    @Bean
    public FilterRegistrationBean hiddenHttpMethodFilter(){
        FilterRegistrationBean hiddenHttpMethodFilter = new FilterRegistrationBean();
        hiddenHttpMethodFilter.setFilter(new HiddenHttpMethodFilter());
        hiddenHttpMethodFilter.setName("hiddenHttpMethodFilter");
        hiddenHttpMethodFilter.addUrlPatterns("/");
        return hiddenHttpMethodFilter;
    }
    spring boot 已经提供了
    */

    /*
    @Bean
    public EmbeddedServletContainerCustomizer
    embeddedServletContainerCustomizer() {
        return container -> container.setContextPath("/forum");
    }//由application.properties替代
    */


}

