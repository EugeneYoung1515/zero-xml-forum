package com.smart.dao;
import java.util.List;

import com.smart.repository.UserRepository;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.smart.domain.User;


/**
 * User对象Dao
 */
@Repository
public class UserDao extends BaseDao<User> {

	@Autowired
	UserRepository userRepository;

   /**
     * 根据用户名查询User对象
     * @param userName 用户名
     * @return 对应userName的User对象，如果不存在，返回null。
     */
	public User getUserByUserName(String userName){
    	return userRepository.findOneByUserName(userName);
	}
	
	/**
	 * 根据用户名为模糊查询条件，查询出所有前缀匹配的User对象
	 * @param userName 用户名查询条件
	 * @return 用户名前缀匹配的所有User对象
	 */
	public List<User> queryUserByUserName(String userName){
		return userRepository.findByUserName(userName);
	}

}
