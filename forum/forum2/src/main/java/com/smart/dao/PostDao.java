package com.smart.dao;

import com.smart.domain.Topic;
import com.smart.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.smart.domain.Post;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Post的DAO类
 *
 */
@Repository
public class PostDao extends BaseDao<Post> {

	@Autowired
	private PostRepository postRepository;

	public Page getPagedPosts(int topicId, int pageNo, int pageSize) {
		Topic topic = new Topic();
		topic.setTopicId(topicId);
		Pageable pageable = new PageRequest(pageNo-1,pageSize,new Sort(Sort.Direction.ASC,"createTime","postId"));
		List<Post> list2 = postRepository.findByTopic(topic,pageable);
		List<Post> list = postRepository.findByTopic(topic);
		return returnNewPage(list,list2,pageNo,pageSize);

		/*
		int listCount = list.size();
		if ( listCount< 1){
			return new Page();
		}else {
			int startIndex = Page.getStartOfPage(pageNo, pageSize);
			return new Page(startIndex, listCount, pageSize, list);
		}
		*/
	}

	/**
	 * 删除主题下的所有帖子
	 * @param topicId 主题ID
	 */
	//@Transactional//(readOnly = false)
	public void deleteTopicPosts(int topicId){
		Topic topic = new Topic();
		topic.setTopicId(topicId);
		int num = postRepository.deleteByTopic(topic);
	}
}
