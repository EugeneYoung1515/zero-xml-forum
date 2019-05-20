package com.smart.redisdao;

import com.smart.domain.User;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

@Repository("redisUserDao")
public class UserDao extends BaseDao<User> {
    @Override
    public int save(User entity) {
        int id = super.save(entity);
        //System.out.println(getRedisTemplate().opsForValue().get(getEntityClassName()+"id"));
        //String id = getRedisTemplate().opsForValue().get(getEntityClassName()+"id");
        HashOperations<String,String,String> hashOperations =  getRedisTemplate().opsForHash();
        hashOperations.put("userName:userId",entity.getUserName(),id+"");
        return id;
    }

    @Override
    public void remove(User entity) {
        super.remove(entity);
        getRedisTemplate().opsForHash().delete("userName:userId",entity.getUserName());
    }

    public User getUserByUserName(String userName){
        HashOperations<String,String,String> hashOperations = getRedisTemplate().opsForHash();
        String id = hashOperations.get("userName:userId",userName);
        if(id==null){
            return null;
        }
        return get(Integer.valueOf(id));
    }
}
