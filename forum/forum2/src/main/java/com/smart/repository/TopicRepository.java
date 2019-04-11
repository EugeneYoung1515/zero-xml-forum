package com.smart.repository;

import com.smart.domain.Topic;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TopicRepository extends BaseRepository<Topic> {
    List<Topic> findByBoardId(int id, Pageable pageable);
    List<Topic> findByBoardId(int id);
    List<Topic> findByTopicTitle(String title,Pageable pageable);
    List<Topic> findByTopicTitle(String title);
}//getPagedTopics queryTopicByTitle
//还是根分页查询相关的
