package com.smart.web.controller;


import com.smart.config.AppConfig;
import com.smart.config.DaoConfig;
import com.smart.config.ServiceConfig;
import com.smart.config.SpringMVCConfig;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DaoConfig.class, ServiceConfig.class, SpringMVCConfig.class})
public class BaseWebTest{

	//声明Request与Response模拟对象
	public MockHttpServletRequest request;
	public MockHttpServletResponse response;
	public MockHttpSession session;
	//spring 自带的模拟类


	//执行测试前先初始模拟对象
	@Before
	public void before() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		session = new MockHttpSession();
		request.setCharacterEncoding("UTF-8");
	}

}
