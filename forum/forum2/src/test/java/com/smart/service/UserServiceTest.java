package com.smart.service;

import com.smart.dao.UserDao;
import com.smart.domain.User;
import com.smart.exception.UserExistException;
import com.smart.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest//这个注释掉 下面的userService之上的@Aotowired就会高亮
//而且跑测试时 会说load ApplicationContext失败
public class UserServiceTest {

    @MockBean
    private UserDao userDao;

    @Autowired
    private UserService userService;
    //private UserService userService = new UserService();//1*或者用这样代替


    @Test
    public void register() throws UserExistException{
        User user = new User();
        user.setUserName("sdjsdj");
        user.setPassword("dsdsdsd");

        //userService.setUserDao(userDao);//1*

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
        }).when(userDao).save(user);

        userService.register(user);
        assertEquals(user.getUserId(), 1);
        verify(userDao, times(1)).save(user);
    }
}
