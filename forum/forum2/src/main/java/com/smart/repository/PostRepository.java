package com.smart.repository;

import com.smart.domain.Post;
import com.smart.domain.Topic;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepository extends BaseRepository<Post> {

    //@Modifying
    //@Transactional
    //@Query("delete from  Post where topic.topicId=:id")
    //int deleteTopicPostsByTopicId(@Param("id") int id);//这个方法没用到

    List<Post> findByTopic(Topic topic, Pageable pageable);
    List<Post> findByTopic(Topic topic);
    int deleteByTopic(Topic topic);
}//少了getPagedPosts
//调了分页查询
