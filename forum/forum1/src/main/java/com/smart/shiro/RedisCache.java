package com.smart.shiro;

import com.smart.redis.FastJson2JsonRedisSerializer;
import com.smart.utils.JdkSerializerUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.*;

//下面的jedis.keys性能不行 可能引起redis阻塞

public class RedisCache<K,V> implements Cache<K,V>{//可以看shiro的javadoc 默认实现
    private JedisPool jedisPool;

    private static final String REDIS_HASH = "shiro:cache:";
    private static final String SPLIT = "@@@@@";

    private String cacheCategory;

    private static final int expireSeconds = 900;//秒 15min

    public RedisCache(JedisPool jedisPool, String cacheCategory) {
        this.jedisPool = jedisPool;
        this.cacheCategory = cacheCategory;
    }

    @Override
    public V get(K key) throws CacheException {
        if(key == null){
            return null;
        }
        Jedis jedis = jedisPool.getResource();
        /*
        String value = jedis.hget(REDIS_HASH+cacheCategory,FastJson2JsonRedisSerializer.toJSONStringIgnoreTransient(key)
                +"@@@@@"+
                        JdkSerializerUtil.objectToString(key));
                        */
        String value = jedis.get(REDIS_HASH+cacheCategory+":"+FastJson2JsonRedisSerializer.toJSONStringIgnoreTransient(key)
                +SPLIT+
                JdkSerializerUtil.objectToString(key));

        jedis.expire(REDIS_HASH+cacheCategory+":"+FastJson2JsonRedisSerializer.toJSONStringIgnoreTransient(key)
                +SPLIT+
                JdkSerializerUtil.objectToString(key),expireSeconds);


        jedis.close();

        //刷新一次主页大概有25次的调用个这个方法 中间夹了一次 RedisSession的update

        //System.out.println(values());

        if(value == null){
            return (V)JdkSerializerUtil.stringToObject(value);
        }
        return (V)JdkSerializerUtil.stringToObject(value.split(SPLIT)[0]);
     }

    @Override
    public V put(K key, V value) throws CacheException {
        V previous = get(key);
        Jedis jedis = jedisPool.getResource();
        /*
        jedis.hset(REDIS_HASH+cacheCategory,FastJson2JsonRedisSerializer.toJSONStringIgnoreTransient(key)
                +"@@@@@"+
                        JdkSerializerUtil.objectToString(key),JdkSerializerUtil.objectToString(value)
                +"@@@@@"+FastJson2JsonRedisSerializer.toJSONStringIgnoreTransient(value)
        );
        */
        jedis.set(REDIS_HASH+cacheCategory+":"+FastJson2JsonRedisSerializer.toJSONStringIgnoreTransient(key)
                +SPLIT+
                JdkSerializerUtil.objectToString(key),

                JdkSerializerUtil.objectToString(value)
                +SPLIT+FastJson2JsonRedisSerializer.toJSONStringIgnoreTransient(value)
        );
        jedis.expire(REDIS_HASH+cacheCategory+":"+FastJson2JsonRedisSerializer.toJSONStringIgnoreTransient(key)
                +SPLIT+
                JdkSerializerUtil.objectToString(key),
                expireSeconds);

        jedis.close();
        return previous;
    }

    /*
    @Override
    public V remove(K key) throws CacheException {
        if(key == null){
            return null;//这样处理好吗
        }
        Jedis jedis = jedisPool.getResource();
        String value = jedis.hget(REDIS_HASH+cacheCategory,FastJson2JsonRedisSerializer.toJSONStringIgnoreTransient(key)
                +"@@@@@"+
                        JdkSerializerUtil.objectToString(key));
        jedis.hdel(REDIS_HASH+cacheCategory,FastJson2JsonRedisSerializer.toJSONStringIgnoreTransient(key)
                +"@@@@@"+
                        JdkSerializerUtil.objectToString(key));
        jedis.close();
        if(value == null){
            return (V)JdkSerializerUtil.stringToObject(value);
        }
        return (V)JdkSerializerUtil.stringToObject(value.split("@@@@@")[0]);
    }
    */

    @Override
    public V remove(K key) throws CacheException {
        if(key == null){
            return null;//这样处理好吗
        }
        Jedis jedis = jedisPool.getResource();
        String value = jedis.get(REDIS_HASH+cacheCategory+":"+FastJson2JsonRedisSerializer.toJSONStringIgnoreTransient(key)
                +SPLIT+
                JdkSerializerUtil.objectToString(key));
        jedis.del(REDIS_HASH+cacheCategory+":"+FastJson2JsonRedisSerializer.toJSONStringIgnoreTransient(key)
                +SPLIT+
                JdkSerializerUtil.objectToString(key));
        jedis.close();
        if(value == null){
            return (V)JdkSerializerUtil.stringToObject(value);
        }
        return (V)JdkSerializerUtil.stringToObject(value.split(SPLIT)[0]);
    }


    @Override
    public void clear() throws CacheException {
        Jedis jedis = jedisPool.getResource();
        //jedis.del(REDIS_HASH+cacheCategory);
        jedis.del(REDIS_HASH+cacheCategory+":*");
        jedis.close();
    }

    @Override
    public int size() {
        Jedis jedis = jedisPool.getResource();
        //int num = jedis.hlen(REDIS_HASH+cacheCategory).intValue();
        Set<String> keys = jedis.keys(REDIS_HASH+cacheCategory+":*");
        int num = keys.size();
        Pipeline pipeline= jedis.pipelined();
        for(String k:keys) {
            pipeline.expire(k,expireSeconds);
        }
        pipeline.sync();
        jedis.close();
        return num;
    }

    /*
    @Override
    public Set<K> keys() {
        Jedis jedis = jedisPool.getResource();
        Set<String> stringSet = jedis.hkeys(REDIS_HASH+cacheCategory);
        Set<K> kSet = new HashSet<>(16);
        if(stringSet!=null && !stringSet.isEmpty()) {
            for (String k : stringSet) {
                kSet.add((K) JdkSerializerUtil.stringToObject(k.split("@@@@@")[1]));
                //kSet.add((K) JdkSerializerUtil.stringToObject(k));

            }
        }
        jedis.close();
        return kSet;
    }
    */

    @Override
    public Set<K> keys() {
        Jedis jedis = jedisPool.getResource();
        Set<String> stringSet = jedis.keys(REDIS_HASH+cacheCategory+":*");
        Set<K> kSet = new HashSet<>(16);
        Pipeline pipeline = jedis.pipelined();
        if(stringSet!=null && !stringSet.isEmpty()) {
            for (String k : stringSet) {
                kSet.add((K) JdkSerializerUtil.stringToObject(k.split(SPLIT)[1]));
                //kSet.add((K) JdkSerializerUtil.stringToObject(k));

                pipeline.expire(k,expireSeconds);
            }
        }
        pipeline.sync();
        jedis.close();
        return kSet;
    }


    /*
    @Override
    public Collection<V> values() {
        Jedis jedis = jedisPool.getResource();
        List<String> stringList = jedis.hvals(REDIS_HASH+cacheCategory);
        List<V> vList = new ArrayList<>(10);
        if(stringList!=null && !stringList.isEmpty()) {
            for (String v : stringList) {
                vList.add((V) JdkSerializerUtil.stringToObject(v.split("@@@@@")[0]));
            }
        }
        jedis.close();
        return vList;
    }
    */

    @Override
    public Collection<V> values() {
        Jedis jedis = jedisPool.getResource();
        Set<String> stringList = jedis.keys(REDIS_HASH+cacheCategory+":*");
        List<V> vList = new ArrayList<>(10);
        Pipeline pipeline = jedis.pipelined();
        Map<String,Response<String>> map = new HashMap<>(16);
        if(stringList!=null && !stringList.isEmpty()) {
            for (String k : stringList) {
                map.put(k,pipeline.get(k));
                pipeline.expire(k,expireSeconds);
            }
        }
        pipeline.sync();
        for(Response<String> response:map.values()){
            vList.add((V) JdkSerializerUtil.stringToObject(response.get().split(SPLIT)[0]));
        }
        jedis.close();

        //System.out.println(vList);
        return vList;
    }


    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

}