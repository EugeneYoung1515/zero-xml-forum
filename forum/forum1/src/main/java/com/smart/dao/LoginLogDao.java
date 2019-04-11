package com.smart.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.smart.domain.LoginLog;
import org.springframework.transaction.annotation.Transactional;

/**
 * Post的DAO类
 *
 */
@Repository
public class LoginLogDao extends BaseDao<LoginLog> {

	@Transactional(readOnly = false)
	public void save(LoginLog loginLog) {
		this.getHibernateTemplate().save(loginLog);
	}

	//private HibernateTemplate hibernateTemplate;
	//@Autowired
	//public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		//this.hibernateTemplate = hibernateTemplate;
	//}

}
