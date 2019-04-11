package com.smart.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.smart.domain.Post;
import org.springframework.transaction.annotation.Transactional;

/**
 * Post的DAO类
 *
 */
@Repository
public class PostDao extends BaseDao<Post> {

	private static final String GET_PAGED_POSTS = "from Post where topic.topicId =? order by createTime desc";

	private static final String DELETE_TOPIC_POSTS = "delete from Post where topic.topicId=?";
	private static final String DELETE_TOPIC_POSTS2 = "delete from  Post where topic.topicId=:param1";
	
	public Page getPagedPosts(int topicId, int pageNo, int pageSize) {
		return pagedQuery(GET_PAGED_POSTS,pageNo,pageSize,topicId);//pagedQuery从父类继承的
	}
    
	/**
	 * 删除主题下的所有帖子
	 * @param topicId 主题ID
	 */


	public void deleteTopicPosts2(int topicId) {
		getHibernateTemplate().bulkUpdate(DELETE_TOPIC_POSTS,topicId);
	}//过期了

	@Transactional(readOnly = false)
	public void deleteTopicPosts(int topicId){
		int i = getHibernateTemplate().execute(session -> session
				.createQuery(DELETE_TOPIC_POSTS2)
				.setInteger("param1",topicId)
				.executeUpdate());
	}//bulkUpdate的实现就像上面的 使用session获得query 之后是query的executeUpdate()方法

	//private HibernateTemplate hibernateTemplate;
	//@Autowired
	//public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		//this.hibernateTemplate = hibernateTemplate;
	//}
}
