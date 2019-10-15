package com.smart.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.ProxiedSession;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class TouchShiroFilterFactoryBean extends ShiroFilterFactoryBean implements FactoryBean, BeanPostProcessor {
    private static transient final Logger log = LoggerFactory.getLogger(TouchShiroFilterFactoryBean.class);

    @Override
    public Class getObjectType() {
        return TouchSpringShiroFilter.class;
    }

    protected AbstractShiroFilter createInstance() throws Exception {

        log.debug("Creating Shiro Filter instance.");

        SecurityManager securityManager = getSecurityManager();
        if (securityManager == null) {
            String msg = "SecurityManager property must be set.";
            throw new BeanInitializationException(msg);
        }

        if (!(securityManager instanceof WebSecurityManager)) {
            String msg = "The security manager does not implement the WebSecurityManager interface.";
            throw new BeanInitializationException(msg);
        }

        FilterChainManager manager = createFilterChainManager();

        //Expose the constructed FilterChainManager by first wrapping it in a
        // FilterChainResolver implementation. The AbstractShiroFilter implementations
        // do not know about FilterChainManagers - only resolvers:
        PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
        chainResolver.setFilterChainManager(manager);

        //Now create a concrete ShiroFilter instance and apply the acquired SecurityManager and built
        //FilterChainResolver.  It doesn't matter that the instance is an anonymous inner class
        //here - we're just using it because it is a concrete AbstractShiroFilter instance that accepts
        //injection of the SecurityManager and FilterChainResolver:
        return new TouchSpringShiroFilter((WebSecurityManager) securityManager, chainResolver);
    }


    private static final class TouchSpringShiroFilter extends AbstractShiroFilter {

        protected TouchSpringShiroFilter(WebSecurityManager webSecurityManager, FilterChainResolver resolver) {
            super();
            if (webSecurityManager == null) {
                throw new IllegalArgumentException("WebSecurityManager property cannot be null.");
            }
            setSecurityManager(webSecurityManager);
            if (resolver != null) {
                setFilterChainResolver(resolver);
            }
        }

        @Override
        protected void updateSessionLastAccessTime(ServletRequest request, ServletResponse response) {
            if (!isHttpSessions()) { //'native' sessions
                Subject subject = SecurityUtils.getSubject();
                //Subject should never _ever_ be null, but just in case:
                if (subject != null) {
                    Session session = subject.getSession(false);
                    if (session != null) {
                        //System.out.println(session.getClass());
                        try {
                            //SimpleSession simpleSession = (SimpleSession)session;


                            ProxiedSession proxiedSession = (ProxiedSession) session;
                            if(System.currentTimeMillis() -proxiedSession.getLastAccessTime().getTime()>=1000) {
                                System.out.println("session.touch()");
                                session.touch();
                            }


                        } catch (Throwable t) {
                            //log.error("session.touch() method invocation has failed.  Unable to update" +
                                    //"the corresponding session's last access time based on the incoming request.", t);
                            //这里可能有问题

                            LoggerFactory.getLogger(AbstractShiroFilter.class).error("session.touch() method invocation has failed.  Unable to update" +
                                    "the corresponding session's last access time based on the incoming request.", t);
                        }
                    }
                }
            }

        }
    }
}
