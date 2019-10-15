
package com.smart.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.smart.serviceinterfaces.UserServiceInterface;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.smart.domain.User;
import com.smart.exception.UserExistException;
import com.smart.service.UserService;

/**
 * 用户注册控制器
 */
@Controller
public class RegisterController extends BaseController {
	/**
	 * 自动注入
	 */
	private UserServiceInterface userService;

	@Autowired
	@Qualifier("redisUserService")
	public void setUserService(UserServiceInterface userService) {
		this.userService = userService;
	}

	/**
	 * 用户登录
	 * @param request
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView register(HttpServletRequest request,User user){
		ModelAndView view = new ModelAndView();
		view.setViewName("/success");
		try {
			user.setPassword(new SimpleHash("MD5",user.getPassword(), ByteSource.Util.bytes(user.getUserName()),3).toHex());
			/*
			盐可以使用用户名等可以确定的东西

			也可以随机生成 再注册时要把随机生成的盐也保存到数据库 realm那里认证时 要把盐取出来 用于加密
			RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
			randomNumberGenerator.nextBytes().toHex()
			 */
			userService.register(user);
		} catch (UserExistException e) {
			view.addObject("errorMsg", "用户名已经存在，请选择其它的名字。");
			view.setViewName("forward:/register.jsp");
		}
		setSessionUser(request,user);
		return view;
	}
	
}
