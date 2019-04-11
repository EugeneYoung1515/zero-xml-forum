package com.smart.dao;

import com.smart.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.smart.domain.Topic;

import java.util.List;

/**
 * topic 的DAO类
 *
 */
@Repository
public class TopicDao extends BaseDao<Topic> {

	@Autowired
	private TopicRepository topicRepository;

	/**
	 * 获取论坛版块分页的主题帖子
	 * @param boardId 论坛版块ID
	 * @param pageNo 页号，从1开始。
	 * @param pageSize 每页的记录数
	 * @return 包含分页信息的Page对象
	 */
	public Page getPagedTopics(int boardId,int pageNo,int pageSize) {
		Pageable pageable = new PageRequest(pageNo-1,pageSize,new Sort(Sort.Direction.DESC,"lastPost"));
		List<Topic> list2 = topicRepository.findByBoardId(boardId,pageable);
		List<Topic> list = topicRepository.findByBoardId(boardId);
		return returnNewPage(list,list2,pageNo,pageSize);
	}
   
	/**
	 * 根据主题帖标题查询所有模糊匹配的主题帖
	 * @param title 标题的查询条件
	 * @param pageNo 页号，从1开始。
	 * @param pageSize 每页的记录数
	 * @return 包含分页信息的Page对象
	 */
	public Page queryTopicByTitle(String title, int pageNo, int pageSize) {
		Pageable pageable = new PageRequest(pageNo-1,pageSize,new Sort(Sort.Direction.DESC,"lastPost"));
		List<Topic> list2 = topicRepository.findByTopicTitle(title,pageable);
		List<Topic> list = topicRepository.findByTopicTitle(title);
		return returnNewPage(list,list2,pageNo,pageSize);
	}
}
