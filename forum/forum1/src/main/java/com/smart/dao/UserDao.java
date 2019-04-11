package com.smart.dao;
import java.util.List;

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
	private static final String GET_USER_BY_USERNAME = "from User u where u.userName = ?";
	private static final String GET_USER_BY_USERNAME2 = "from User u where u.userName = :param1";
	
	private static final String QUERY_USER_BY_USERNAME = "from User u where u.userName like ?";
	private static final String QUERY_USER_BY_USERNAME2 = "from User u where u.userName like :param1";
	
    /**
     * 根据用户名查询User对象
     * @param userName 用户名
     * @return 对应userName的User对象，如果不存在，返回null。
     */
	public User getUserByUserName2(String userName){
	    List<User> users = (List<User>)getHibernateTemplate().find(GET_USER_BY_USERNAME,userName);
	    if (users.size() == 0) {
			return null;
		}else{
			return users.get(0);
		}
    }
    public User getUserByUserName(final String userName){
		List<User> users = (List<User>)getHibernateTemplate().execute(session ->{
			Query query = session.createQuery(GET_USER_BY_USERNAME2);
			query.setString("param1",userName);
			return query.list();});
		if (users.size() == 0) {
			return null;
		}else{
			return users.get(0);
		}
	}
	
	/**
	 * 根据用户名为模糊查询条件，查询出所有前缀匹配的User对象
	 * @param userName 用户名查询条件
	 * @return 用户名前缀匹配的所有User对象
	 */
	public List<User> queryUserByUserName2(String userName){
	    return (List<User>)getHibernateTemplate().find(QUERY_USER_BY_USERNAME,userName+"%");
	}

	public List<User> queryUserByUserName(String userName){
		String userName2 = userName+"%";
		return (List<User>)getHibernateTemplate().execute(session ->
				session.createQuery(QUERY_USER_BY_USERNAME2)
						.setString("param1",userName2)
						.list());
	}

	//private HibernateTemplate hibernateTemplate;
	//@Autowired
	//ublic void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		//this.hibernateTemplate = hibernateTemplate;
	//}
}
