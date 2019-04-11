package com.smart.shiro;
//报名全部大写

import org.apache.shiro.authc.UsernamePasswordToken;

public class CaptchaUsernamePasswordToken extends UsernamePasswordToken {

    private String captcha;

// 省略 getter 和 setter 方法

    public CaptchaUsernamePasswordToken(String userName, String password,
                                        boolean rememberMe,String host,String captcha) {
        super(userName, password, rememberMe,host);
        this.captcha = captcha;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
