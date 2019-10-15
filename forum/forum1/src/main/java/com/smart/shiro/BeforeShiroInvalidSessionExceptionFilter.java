package com.smart.shiro;

import org.apache.shiro.session.InvalidSessionException;

import javax.servlet.*;
import java.io.IOException;

public class BeforeShiroInvalidSessionExceptionFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try{
            filterChain.doFilter(servletRequest,servletResponse);
        }catch (InvalidSessionException ex){//这个个看起来不会发生
            forward(servletRequest,servletResponse);
        }catch (IllegalStateException ex){//这个个看起来不会发生
            if(getCause(ex) instanceof InvalidSessionException){
                forward(servletRequest,servletResponse);
            }else{
                throw ex;
            }

        }catch (ServletException ex){
            if(getCause(ex) instanceof InvalidSessionException){
                forward(servletRequest,servletResponse);
            }else{
                throw ex;
            }

        }
    }
    //问题 jsp里会丢InvalidSessionException吗
    //如果丢出 InvalidSessionException tomcat容器是怎么处理的

    private Throwable getCause(Throwable e) {
        Throwable cause = null;
        Throwable result = e;

        while(null != (cause = result.getCause())  && (result != cause) ) {
            result = cause;
        }
        return result;
    }

    private void forward(ServletRequest request,ServletResponse response)throws IOException, ServletException{
        request.setAttribute("errorMsg","会话过期，请重新登录");
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request,response);
    }

    @Override
    public void destroy() {

    }
}
