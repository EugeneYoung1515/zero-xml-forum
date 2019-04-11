package com.smart.dao;

import com.smart.config.DaoConfig;
import com.smart.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoConfig.class)
public class MyTest {
    @Autowired
    private BoardDao boardDao;

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserDao userDao;

    @Test
    public void find(){
        User user = userDao.getUserByUserName("hhh");
        assertEquals(user.getCredit(),100);
    }
}
