package com.smart.shiro;

import com.smart.redis.FastJson2JsonRedisSerializer;
import com.smart.utils.JdkSerializerUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.Serializable;

public class RedisSessionDAOJedis {
    private JedisPool jedisPool;
    private FastJson2JsonRedisSerializer<SimpleSession> serializerSimpleSession;
    private static final String SPLIT = "@@@@";

    public RedisSessionDAOJedis(JedisPool jedisPool, FastJson2JsonRedisSerializer<SimpleSession> serializerSimpleSession) {
        this.jedisPool = jedisPool;
        this.serializerSimpleSession = serializerSimpleSession;
    }

    public void create(String sessionKey, Session session){
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(sessionKey, JdkSerializerUtil.objectToString(session)+SPLIT+serializerSimpleSession.serializeToString((SimpleSession)session));
        }finally {
            jedis.close();
        }

    }

    public Session read(String sessionKey){
        Jedis jedis = jedisPool.getResource();
        try {
            String result = jedis.get(sessionKey);
            if(result!=null) {
                return (SimpleSession) JdkSerializerUtil.stringToObject(result.split(SPLIT)[0]);
            }
            return null;
        }finally {
            jedis.close();
        }
    }

    public void update(String sessionKey,Session session){
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(sessionKey, JdkSerializerUtil.objectToString(session)+SPLIT+serializerSimpleSession.serializeToString((SimpleSession)session));
        }finally {
            jedis.close();
        }
    }

    public void delete(String sessionKey){
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(sessionKey);
        }finally {
            jedis.close();
        }
    }
}
