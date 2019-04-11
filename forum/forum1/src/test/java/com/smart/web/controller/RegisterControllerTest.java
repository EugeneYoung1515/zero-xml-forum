package com.smart.web.controller;

import com.smart.domain.User;
import com.smart.web.RegisterController;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@WebAppConfiguration//要补上这个注解才可以
public class RegisterControllerTest extends BaseWebTest {
	@Autowired
	private RegisterController controller;

	@Test
	public void register() throws Exception {
		request.setRequestURI("/register.html");
		request.setMethod("POST");

		User user = new User();
		user.setUserName("test");
		user.setPassword("1234");
		user.setLocked(0);

		// 向控制发起请求 
		ModelAndView mav = controller.register(request,user);
		assertNotNull(mav);
		assertEquals("forward:/register.jsp",mav.getViewName());
	}
	//上面是使用mock类的分离测试
	//要使用集成测试
	//可以使用RestTemplate
	//或者使用Postman
}
