package com.smart.web;

import com.smart.domain.User;
import com.smart.exception.UserExistException;
import com.smart.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest

@RunWith(SpringRunner.class)
@WebMvcTest(RegisterController.class)
@AutoConfigureMockMvc(addFilters = false)//没有这个注解属性 会说ServletContext not found 也就是shiro的问题

//@SpringBootTest+@AutoConfigureMockMvc(addFilters = false)
//或者@WebMvcTest(RegisterController.class)+@AutoConfigureMockMvc(addFilters = false)
// @AutoConfigureMockMvc(addFilters = false)重写@WebMvcTest(RegisterController.class)的配置

public class RegisterControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RegisterController registerController;

    @MockBean
    private UserService userService;//没有这个下面的control中注入的就是真实的UserService

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    public void register() throws Exception {
        User user = new User();
        user.setUserName("alalalalsds");
        user.setPassword("123456");

        //doNothing().when(userService).register(user);

        doThrow(new UserExistException("用户名已经存在")).when(userService).register(user);
        //这个能起效果的


        /*
        doAnswer(new Answer<User>() {
            public User answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                User user = (User) args[0];
                if (user != null) {
                    user.setUserId(1);
                    System.out.println(12);
                }
                return user;
            }//上面相当于把save方法的内容换掉了
        }).when(userService).register(user);
        *///这个也能起效果

        //mvc.perform(post("/register.html",user)
        mvc.perform(post("/register.html").param("userName","alalalalsds").param("password","123456").accept(MediaType.TEXT_HTML_VALUE)).andExpect(status().isOk()).andExpect(model().attribute("user",user));

        //  /forum/register.html就不行
        //这里似乎有问题 运行测试类时

        /*
        ModelAndView:
        View name = /success
                View = null
        Attribute = user
        value = com.smart.domain.User@6f1ab210[userId=0,userName=<null>,userType=1,lastIp=<null>,lastVisit=<null>,password=<null>,locked=0,credit=0,manBoards=[]]
        errors = []
        */

        //使用param("userName","alalalalsds").param("password","123456") 最终不是同一个对象但是 属性是对的

        request.setCharacterEncoding("UTF-8");
        ModelAndView modelAndView=registerController.register(request,user);
        assertEquals("forward:/register.jsp",modelAndView.getViewName());

        //原因大概知道了 mvc.perform(post("/register.html",user)中传user进去 和传给userService的user不是同一个user

        doThrow(new UserExistException("用户名已经存在")).when(userService).register(any(User.class));
        mvc.perform(post("/register.html").accept(MediaType.TEXT_HTML_VALUE)).andExpect(status().isOk());
    }

}
