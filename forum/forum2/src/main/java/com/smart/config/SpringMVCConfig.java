package com.smart.config;

import com.smart.web.ForumHandlerExceptionResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;
import java.util.Properties;

@Configuration
//@ComponentScan("com.smart.web")
//@EnableWebMvc//使用application。properties就要注释掉
public class SpringMVCConfig extends WebMvcConfigurerAdapter{

    /*
    @Bean
    public InternalResourceViewResolver internalResourceViewResolver(){
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/WEB-INF/jsp/");
        internalResourceViewResolver.setSuffix(".jsp");
        return internalResourceViewResolver;
    }//由application.properties替代
    */

    @Bean
    public ForumHandlerExceptionResolver forumHandlerExceptionResolver(){
        ForumHandlerExceptionResolver forumHandlerExceptionResolver = new ForumHandlerExceptionResolver();
        forumHandlerExceptionResolver.setDefaultErrorView("fail");
        Properties properties = new Properties();
        properties.put("java.lang.RuntimeException","fail");
        forumHandlerExceptionResolver.setExceptionMappings(properties);
        return forumHandlerExceptionResolver;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //registry.addViewController("/").setViewName("index.jsp");
        registry.addViewController("/").setViewName("index");
    }

/*
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(){
        return new MappingJackson2HttpMessageConverter();
    }//这个springboot默认提供了

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters){
        converters.add(mappingJackson2HttpMessageConverter());
    }
    */
}
