package com.smart.config;
import com.smart.shiro.AfterShiroFilter;
import com.smart.shiro.BeforeShiroInvalidSessionExceptionFilter;
import com.smart.web.ForumFilter;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import java.util.EnumSet;

public class WebServletConfig implements WebApplicationInitializer {
    public void onStartup(ServletContext servletContext){
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(com.smart.config.AppConfig.class);
        servletContext.addListener(new ContextLoaderListener(ctx));
        //需要上面这一句 不然web层bean需要注入的业务层bean找不到
        //上面这一句相当于是把web.xml的内容翻译过来了
        //似乎还可以换成其他有类似作用的语句

        //不过下面的spring mvc上下文为什么不需要
        AnnotationConfigWebApplicationContext WebMVCConfig = new AnnotationConfigWebApplicationContext();
        WebMVCConfig.register(com.smart.config.SpringMVCConfig.class);

        FilterRegistration.Dynamic hibernateFilter = servletContext.addFilter("hibernateFilter",new OpenSessionInViewFilter());
        hibernateFilter.setInitParameter("sessionFactoryBeanName","sessionFactory");
        hibernateFilter.setInitParameter("singleSession","true");
        hibernateFilter.setInitParameter("flushMode","AUTO");
        hibernateFilter.addMappingForUrlPatterns(null,true,"/*");

        //后面补充的 为了处理InvalidSession异常
        FilterRegistration.Dynamic beforeShiroInvalidSessionExceptionFilter = servletContext.addFilter("beforeShiroInvalidSessionExceptionFilter",new BeforeShiroInvalidSessionExceptionFilter());
        beforeShiroInvalidSessionExceptionFilter.addMappingForUrlPatterns(null,true,"/*");

        FilterRegistration.Dynamic shiroFilter = servletContext.addFilter("shiroFilter",new DelegatingFilterProxy());
        shiroFilter.setInitParameter("targetFilterLifecycle","true");
        shiroFilter.addMappingForUrlPatterns(null,true,"/*");

        FilterRegistration.Dynamic afterShiroFilter = servletContext.addFilter("afterShiroFilter",new AfterShiroFilter());
        afterShiroFilter.addMappingForUrlPatterns(null,true,"/*");

        FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encodingFilter",new CharacterEncodingFilter());
        encodingFilter.setInitParameter("encoding","UTF-8");
        encodingFilter.setInitParameter("forceEncoding","true");
        encodingFilter.addMappingForUrlPatterns(null,true,"/*");

        ServletRegistration.Dynamic xiaochun = servletContext.addServlet("xiaochun",new DispatcherServlet(WebMVCConfig));
        xiaochun.setLoadOnStartup(3);
        xiaochun.addMapping("/");
/*
<servlet-mapping>
		<servlet-name>default</servlet-name>
		<url-pattern>/static/</url-pattern>
	</servlet-mapping>

	上面这个还没处理
 */
        FilterRegistration.Dynamic HiddenHttpMethodFilter = servletContext.addFilter("HiddenHttpMethodFilter",new HiddenHttpMethodFilter());
        HiddenHttpMethodFilter.addMappingForServletNames(null,true,"xiaochun");
    }
}
