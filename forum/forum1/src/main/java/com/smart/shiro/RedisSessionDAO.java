package com.smart.shiro;

import com.smart.redis.FastJson2JsonRedisSerializer;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import static com.smart.cons.CommonConstant.USER_CONTEXT;

//这里是把redis当数据库 RedisCache那里是把redis当缓存
public class RedisSessionDAO extends EnterpriseCacheSessionDAO {

    //session在redis中的过期时间:30分钟 30*60s
    private static final int expireTime = 1800;

    //redis中session名称前缀
    private static String prefix = "sessionId:";

    //private JedisPool jedisPool;

    //private FastJson2JsonRedisSerializer<Session> serializer;

    //private FastJson2JsonRedisSerializer<SimpleSession> serializerSimpleSession;

    private RedisSessionDAOJedis redisSessionDAOJedis;

    // 创建session，保存到数据库
    @Override
    protected Serializable doCreate(Session session) {
        System.out.println("create");
        Serializable sessionId = super.doCreate(session);
        redisSessionDAOJedis.create(prefix+sessionId,session);
        return sessionId;
    }

    // 获取session
    @Override
    protected Session doReadSession(Serializable sessionId) {
        System.out.println("read");

        // 先从缓存中获取session，如果没有再去数据库中获取
        Session session = super.doReadSession(sessionId);
        if(session == null){
            return redisSessionDAOJedis.read(prefix+sessionId);
        }
        return session;
    }
    //这里比较奇怪 Rediscache过期了 这里会有非常多的调用 也就是 doReadSession会有非常多的调用
    //原因shiro的实现 是从数据库读 不会加入到缓存中


    // 更新session的最后一次访问时间
    @Override
    protected void doUpdate(Session session) {
        System.out.println("update");
        super.doUpdate(session);
        redisSessionDAOJedis.update(prefix+session.getId(),session);
    }

    //删除session
    @Override
    protected void doDelete(Session session) {
        System.out.println("delete");
        super.doDelete(session);
        redisSessionDAOJedis.delete(prefix+session.getId());
    }

    //为了防止频繁更新 touch



    @Override
    public void update(Session session) throws UnknownSessionException {
        TouchSimpleSession2 touchSimpleSession = (TouchSimpleSession2)session;
        if(!touchSimpleSession.isChanged() && (System.currentTimeMillis() - touchSimpleSession.getLastTouchTime().getTime() < 1000)){
            System.out.println("noChange");
            return;
        }
        //(System.currentTimeMillis() - touchSimpleSession.getLastAccessTime().getTime() < 1000)
        //后面这一句一定会成立 应为touch方法的调用和这一句之间的间隔 时间会少于一秒

        if(touchSimpleSession.isChanged()) {
            touchSimpleSession.touch();
        }

        touchSimpleSession.setChanged(true);
        doUpdate(session);
        if (session instanceof ValidatingSession) {
            if (((ValidatingSession) session).isValid()) {
                cache(session, session.getId());
            } else {
                uncache(session);
            }
        } else {
            cache(session, session.getId());
        }
    }


    /*
    @Override
    public void update(Session session) throws UnknownSessionException {
        doUpdate(session);
        if (session instanceof ValidatingSession) {
            if (((ValidatingSession) session).isValid()) {
                cache(session, session.getId());
            } else {
                uncache(session);
            }
        } else {
            cache(session, session.getId());
        }
    }

    @Override
    protected void uncache(Session session) {
        if (session == null) {
            System.out.println("session==null");
            return;
        }
        Serializable id = session.getId();
        if (id == null) {
            System.out.println("id==null");
            return;
        }
        Cache<Serializable, Session> cache = getActiveSessionsCacheLazy();
        if (cache != null) {
            System.out.println("cache!=null&&remove(id)");
            cache.remove(id);
        }
    }

    @Override
    protected void cache(Session session, Serializable sessionId) {
        if (session == null || sessionId == null) {
            System.out.println("session == null || sessionId == null");
            return;
        }
        Cache<Serializable, Session> cache = getActiveSessionsCacheLazy();
        if (cache == null) {
            System.out.println("cache == null");
            return;
        }
        cache(session, sessionId, cache);
    }

    @Override
    protected void cache(Session session, Serializable sessionId, Cache<Serializable, Session> cache) {
        System.out.println("Session session, Serializable sessionId, Cache<Serializable, Session> cache");
        cache.put(sessionId, session);
    }

    private Cache<Serializable, Session> getActiveSessionsCacheLazy() {
        if (this.getActiveSessionsCache() == null) {
            //this.activeSessions = createActiveSessionsCache();
            this.setActiveSessionsCache(createActiveSessionsCache());
        }
        //return activeSessions;
        return getActiveSessionsCache();
    }

    @Override
    protected Cache<Serializable, Session> createActiveSessionsCache() {
        Cache<Serializable, Session> cache = null;
        CacheManager mgr = getCacheManager();
        if (mgr != null) {
            String name = getActiveSessionsCacheName();
            cache = mgr.getCache(name);
        }
        return cache;
    }
    */

    /*
    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }


    public void setSerializer(FastJson2JsonRedisSerializer<Session> serializer) {
        this.serializer = serializer;
    }


    public void setSerializerSimpleSession(FastJson2JsonRedisSerializer<SimpleSession> serializerSimpleSession) {
        this.serializerSimpleSession = serializerSimpleSession;
    }
    */

    public void setRedisSessionDAOJedis(RedisSessionDAOJedis redisSessionDAOJedis) {
        this.redisSessionDAOJedis = redisSessionDAOJedis;
    }
}