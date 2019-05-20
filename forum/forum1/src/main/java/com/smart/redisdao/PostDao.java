package com.smart.redisdao;

import com.smart.dao.Page;
import com.smart.domain.Post;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository("redisPostDao")
public class PostDao extends BaseDao<Post> {
    @Override
    public int save(Post entity) {
        int id = super.save(entity);
        //String id = getRedisTemplate().opsForValue().get(getEntityClassName()+"id");
        getRedisTemplate().opsForSet().add("topicId:postId:"+entity.getTopic().getTopicId(),id+"");
        getRedisTemplate().opsForZSet().add("postId:timestamp:",id+"",entity.getCreateTime().getTime());

        getRedisTemplate().expire("topicId:postId:postId:timestamp:"+entity.getTopic().getTopicId(),0, TimeUnit.SECONDS);
        getRedisTemplate().expire("boardId:topicId:topicId:timestamp:"+entity.getBoardId(),0, TimeUnit.SECONDS);
        return id;
        //上面两个用来交集
    }//remove上面的键要不要删除

    @Override
    public void update(Post entity) {
        super.update(entity);
        //getRedisTemplate().opsForZSet().add("postId:timestamp:",entity.getPostId()+"",entity.getCreateTime().getTime());
    }

    @Override
    public void remove(Post entity) {
        super.remove(entity);
        getRedisTemplate().opsForSet().remove("topicId:postId:"+entity.getTopic().getTopicId(),id(entity)+"");//没加双引号 变成字符串 就移除不了
        getRedisTemplate().opsForZSet().remove("postId:timestamp:",id(entity)+"");//没加双引号 变成字符串 就移除不了
    }

    public Page getPagedPosts(int topicId, int pageNo, int pageSize) {
        List<String> list = new ArrayList<>(10);
        list.add("postId:timestamp:");
        if(!getRedisTemplate().hasKey("topicId:postId:postId:timestamp:"+topicId)) {
            intersectAndStore("topicId:postId:" + topicId, list, "topicId:postId:postId:timestamp:" + topicId, RedisZSetCommands.Aggregate.MAX, new int[]{1, 1});
            getRedisTemplate().expire("topicId:postId:postId:timestamp:"+topicId,60, TimeUnit.SECONDS);
        }
        return pagedQuery("topicId:postId:postId:timestamp:"+topicId,pageNo,pageSize);
    }

    public void deleteTopicPosts(int topicId){
        Set<String> postIds = getRedisTemplate().opsForSet().members("topicId:postId:"+topicId);
        String[] postIdsString = postIds.toArray(new String[postIds.size()]);
        getRedisTemplate().opsForZSet().remove("postId:timestamp:",postIdsString);
        getRedisTemplate().expire("topicId:postId:"+topicId,0, TimeUnit.SECONDS);
        for(String id:postIds){
            getRedisTemplate().opsForSet().remove(getEntityClassName()+"all:id",id);
            getRedisTemplate().expire(getEntityClassName()+id,0, TimeUnit.SECONDS);
        }
    }
}
