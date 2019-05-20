package com.smart.redisdao;

import com.smart.dao.Page;
import com.smart.domain.Topic;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository("redisTopicDao")
public class TopicDao extends BaseDao<Topic> {
    @Override
    public int save(Topic entity) {
        int id = super.save(entity);
        //String id = getRedisTemplate().opsForValue().get(getEntityClassName()+"id");
        getRedisTemplate().opsForSet().add("boardId:topicId:"+entity.getBoardId(),id+"");
        getRedisTemplate().opsForZSet().add("topicId:timestamp:",id+"",entity.getLastPost().getTime());

        getRedisTemplate().expire("boardId:topicId:topicId:timestamp:"+entity.getBoardId(),0, TimeUnit.SECONDS);
        return id;
    }

    @Override
    public void update(Topic entity) {
        super.update(entity);
        getRedisTemplate().opsForZSet().add("topicId:timestamp:",entity.getTopicId()+"",entity.getLastPost().getTime());
    }

    @Override
    public void remove(Topic entity) {
        super.remove(entity);
        getRedisTemplate().opsForSet().remove("boardId:topicId:"+entity.getBoardId(),id(entity)+"");
        System.out.println("boardId:topicId:"+entity.getBoardId()+" "+id(entity));
        getRedisTemplate().opsForZSet().remove("topicId:timestamp:",id(entity)+"");
        System.out.println("topicId:timestamp:"+id(entity));

        getRedisTemplate().expire("boardId:topicId:topicId:timestamp:"+entity.getBoardId(),0, TimeUnit.SECONDS);
    }

    public Page getPagedTopics(int boardId, int pageNo, int pageSize) {
        List<String> list = new ArrayList<>(10);
        list.add("topicId:timestamp:");
        if(!getRedisTemplate().hasKey("boardId:topicId:topicId:timestamp:"+boardId)) {
            intersectAndStore("boardId:topicId:"+boardId, list, "boardId:topicId:topicId:timestamp:"+boardId, RedisZSetCommands.Aggregate.MAX, new int[]{1, 1});
            getRedisTemplate().expire("boardId:topicId:topicId:timestamp:"+boardId,60, TimeUnit.SECONDS);
        }
        return pagedQuery("boardId:topicId:topicId:timestamp:"+boardId,pageNo,pageSize);
    }

}
