package com.smart.service;


import com.smart.dao.UserDao;
import com.smart.domain.User;
import com.smart.exception.UserExistException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UserServiceTest extends BaseService {
    private UserDao userDao;
    private UserService userService;

    @Before
    public void init(){
        userDao = Mockito.mock(UserDao.class);
        userService = new UserService();
        ReflectionTestUtils.setField(userService,"userDao",userDao);
        //上面几步是为了做分离测试
        //userDao是模拟出来的
        //如果userservice是用注入的
        //那么userService使用的userDao也是注入的 不是上面的模拟出来的
        //所以userService要用new的
        //然后用ReflectionTestUtils.setField给私有的实例变量赋值（赋模拟的userDao）
    }

    @Test
    public void register() throws UserExistException{
        User user = new User();
        user.setUserName("test");
        user.setPassword("1234");

        doAnswer(new Answer<User>() {
            public User answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                User user = (User) args[0];
                if (user != null) {
                    user.setUserId(1);
                }
                return user;
            }//上面相当于把save方法的内容换掉了
        }).when(userDao).save(user);

        userService.register(user);
        assertEquals(user.getUserId(), 1);
        verify(userDao, times(1)).save(user);
    }
}
