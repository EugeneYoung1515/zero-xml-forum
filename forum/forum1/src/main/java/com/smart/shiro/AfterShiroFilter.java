package com.smart.shiro;

import com.smart.domain.User;
import com.smart.serviceinterfaces.UserServiceInterface;
import com.smart.utils.RequestHolderUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

import static com.smart.cons.CommonConstant.USER_CONTEXT;

public class AfterShiroFilter implements Filter{

    private UserServiceInterface userService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext sc = filterConfig.getServletContext();
        AnnotationConfigWebApplicationContext cxt = (AnnotationConfigWebApplicationContext) WebApplicationContextUtils.getWebApplicationContext(sc);

        if(cxt != null && cxt.getBean("redisUserService") != null && userService == null) {
            userService = (UserServiceInterface) cxt.getBean("redisUserService");
            System.out.println(userService);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        User userSession = (User) ((HttpServletRequest)servletRequest).getSession().getAttribute(USER_CONTEXT);
        //User userSession = (User)SecurityUtils.getSubject().getSession().getAttribute(USER_CONTEXT);
        if(userSession==null) {
        Subject subject = SecurityUtils.getSubject();
            if (subject.isAuthenticated() || subject.isRemembered()) {
                Object principal = subject.getPrincipal();
                if (principal != null) {
                    User user = (User) principal;
                    User user1 = userService.getUserByUserName(user.getUserName());
                    user1.setLastIp(((HttpServletRequest)servletRequest).getRemoteAddr());
                    user1.setLastVisit(new Date());
                    userService.loginSuccess(user1);
                    Session session = subject.getSession();
                    session.setAttribute(USER_CONTEXT, user1);
                 }
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
