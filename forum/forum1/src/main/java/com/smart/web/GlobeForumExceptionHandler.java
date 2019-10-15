package com.smart.web;


import org.apache.shiro.session.InvalidSessionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;

@ControllerAdvice
public class GlobeForumExceptionHandler {

    /*
    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleException(RuntimeException e) {
        System.out.println("打印异常信息：");
        e.printStackTrace();
        ModelAndView modelAndView = new ModelAndView("/fail");
        modelAndView.addObject("errorMsg",e);
        return modelAndView;
    }
    */

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleException(Exception e) {
        System.out.println("打印异常信息：");
        e.printStackTrace();
        ModelAndView modelAndView = new ModelAndView("/fail");

        String s = "会话过期，请重新登录";
        if(e instanceof InvalidSessionException){
            System.out.println("会话异常");
            modelAndView.addObject("errorMsg",s);

        }else if(e instanceof IllegalStateException){
            if(e.getCause() instanceof InvalidSessionException){
                System.out.println("会话异常");
                modelAndView.addObject("errorMsg",s);

            }
        }/*else if(e instanceof ServletException){//这个应该是不会发生
            Throwable t = e.getCause();
            if(t instanceof InvalidSessionException){
                System.out.println("会话异常");
                modelAndView.addObject("errorMsg",s);

            }else if(t instanceof IllegalStateException){
                if(t.getCause() instanceof InvalidSessionException){
                    System.out.println("会话异常");
                    modelAndView.addObject("errorMsg",s);
                }
            }
        }*/else{
            modelAndView.addObject("errorMsg",e);
        }

        return modelAndView;
    }

}
