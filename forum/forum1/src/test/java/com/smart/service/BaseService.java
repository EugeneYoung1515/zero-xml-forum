package com.smart.service;


import com.smart.config.DaoConfig;
import com.smart.config.ServiceConfig;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DaoConfig.class, ServiceConfig.class})
public class BaseService {
    @Autowired
    HibernateTemplate hibernateTemplate;
}
