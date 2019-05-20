package com.smart.web;

import com.smart.cons.CommonConstant;
import com.smart.domain.User;
import com.smart.service.UserService;
import com.smart.serviceinterfaces.UserServiceInterface;
import com.smart.shiro.IncorrectCaptchaException;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 *   论坛管理，这部分功能由论坛管理员操作，包括：创建论坛版块、指定论坛版块管理员、
 * 用户锁定/解锁。
 */
@Controller
@RequestMapping("/login")
public class ShiroLoginController extends BaseController {

	private UserServiceInterface userService;

	@Autowired
	@Qualifier("redisUserService")
	public void setUserService(UserServiceInterface userService) {
		this.userService = userService;
	}

	/**
	 * 用户登陆
	 * @param request
	 * @param user
	 * @return
	 */

	/*
	@RequestMapping("/doLogin")
	public ModelAndView login(HttpServletRequest request, User user) {
		User dbUser = userService.getUserByUserName(user.getUserName());
		ModelAndView mav = new ModelAndView();
		mav.setViewName("forward:/login.jsp");
		if (dbUser == null) {
			mav.addObject("errorMsg", "用户名不存在");
		} else if (!dbUser.getPassword().equals(user.getPassword())) {
			mav.addObject("errorMsg", "用户密码不正确");
		} else if (dbUser.getLocked() == User.USER_LOCK) {
			mav.addObject("errorMsg", "用户已经被锁定，不能登录。");
		} else {
			dbUser.setLastIp(request.getRemoteAddr());
			dbUser.setLastVisit(new Date());
			userService.loginSuccess(dbUser);
			setSessionUser(request,dbUser);
			String toUrl = (String)request.getSession().getAttribute(CommonConstant.LOGIN_TO_URL);
			request.getSession().removeAttribute(CommonConstant.LOGIN_TO_URL);
			//如果当前会话中没有保存登录之前的请求URL，则直接跳转到主页
			if(StringUtils.isEmpty(toUrl)){
				toUrl = "/index.html";
			}
			mav.setViewName("redirect:"+toUrl);
		}
		return mav;
	}
	*/
	/**
	 * 用户登陆
	 * @param request
	 * @param user
	 * @return
	 */

	@RequestMapping("/doLogin")
	public ModelAndView login(HttpServletRequest request, User user,boolean rememberMe) {//能设置rememberMe cookies到浏览器中 但是好像不起作用 rememberMe cookies似乎要结合过滤器才能起作用 能起作用 4月10日改
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(user.getUserName(),user.getPassword(),rememberMe);

		//if(!subject.isRemembered()) {//这个remember能起效果 就是第一次登录点击记住 之后cm+q退出浏览器 重新进入登录界面 什么都不填 直接点登入按钮 beforeReturnIndex下面的beforeReturnIndex会被打印出来 4月10日
		ModelAndView mav = new ModelAndView();
			try {
				String captcha = (String) request.getSession().getAttribute("Captcha");
				if (captcha == null || !captcha.equals(request.getParameter("captcha"))) {
					System.out.println(request.getParameter("captcha"));
					throw new IncorrectCaptchaException();
				}
				subject.login(usernamePasswordToken);
				User dbUser = userService.getUserByUserName(user.getUserName());
				dbUser.setLastIp(request.getRemoteAddr());
				dbUser.setLastVisit(new Date());
				userService.loginSuccess(dbUser);
				setSessionUser(request, dbUser);

				String toUrl = (String)request.getSession().getAttribute(CommonConstant.LOGIN_TO_URL);
				request.getSession().removeAttribute(CommonConstant.LOGIN_TO_URL);
				request.getSession().removeAttribute("@@session_context_filtered_request");

				if(StringUtils.isEmpty(toUrl)){
					toUrl = "/index.html";
				}
				mav.setViewName("redirect:"+toUrl);

				System.out.println("beforeCatch");

			} catch (IncorrectCaptchaException e) {
				//request.setAttribute("shiroLoginFailure",e.toString());
				request.setAttribute("shiroLoginFailure", "验证码错误");
				//return new ModelAndView("login");
				return new ModelAndView("forward:/login.jsp");
			} catch (UnknownAccountException e) {
				//request.setAttribute("shiroLoginFailure",e.toString());
				request.setAttribute("shiroLoginFailure", "用户名不存在");
				//return new ModelAndView("login");
				return new ModelAndView("forward:/login.jsp");
			} catch (IncorrectCredentialsException e) {
				//request.setAttribute("shiroLoginFailure",e.toString());
				request.setAttribute("shiroLoginFailure", "用户密码不正确");
				//return new ModelAndView("login");
				return new ModelAndView("forward:/login.jsp");
			} catch (LockedAccountException e) {
				//request.setAttribute("shiroLoginFailure",e.toString());
				request.setAttribute("shiroLoginFailure", "用户已经被锁定，不能登录。");
				//return new ModelAndView("login");
				return new ModelAndView("forward:/login.jsp");
			}
		//}
		System.out.println("beforeReturnIndex");
		//return new ModelAndView("redirect:"+"/index.html");

		return mav;
	}

	/*这样可以 但是登录成功了不能设置会话属性
	@RequestMapping("/login")
	public String login(){
		return "login";
	}
	*/

	/**
	 * 登录注销
	 * @param session
	 * @return
	 */

	/*//交给shiro的logout过滤器
	@RequestMapping("/doLogout")
	public String logout(HttpSession session) {
		//session.removeAttribute(CommonConstant.USER_CONTEXT);
		return "forward:/index.jsp";
	}
	*/

}
