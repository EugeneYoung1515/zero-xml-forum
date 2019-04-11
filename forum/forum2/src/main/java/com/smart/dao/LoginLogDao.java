package com.smart.dao;

import com.smart.repository.BaseRepository;
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

	@Autowired
	BaseRepository<LoginLog> baseRepository;

	//@Transactional//(readOnly = false)
	public void save(LoginLog loginLog){
		baseRepository.save(loginLog);
	}
}
