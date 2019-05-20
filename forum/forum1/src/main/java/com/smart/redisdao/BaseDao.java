package com.smart.redisdao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.dao.Page;
import com.smart.utils.ReflectionUtil;
import com.smart.utils.ReflectionUtil2;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


/*
1.
时间序列化反序列化的问题解决了
一直出问题 是因为 update那里自己一直使用BeanUtils的序列化方法

ObjectMapper的setDateFormat和自定义时间反序列化器都是可行的

2.自己的ReflectionUtil和ReflectionUtil2都是可行的
ReflectionUtil是记录多对一中的一方id ReflectionUtil2是嵌套赋值

3.ObjectMapper的convertValue也能嵌套赋值
4.联合使用ObjectMapper的writeValueAsString和readValue在bean json map中转换 也能 嵌套赋值
那为什么能搜到使用Jackson json(或者map)转bean 需要麻烦一点 的文章或者讨论？

5.也就是现在
自己的ReflectionUtil和ReflectionUtil2都是可行的
ObjectMapper的convertValue是可行的
联合使用ObjectMapper的writeValueAsString和readValue是可行的
 */
public class BaseDao<T> {
    private RedisTemplate<String,String> redisTemplate;
    private String entityClassName;
    private Class<T> entityClass;
    private String primaryKeyName;

    public BaseDao() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClass = (Class) params[0];
        entityClassName = entityClass.getSimpleName()+":";

        Field[] fields = entityClass.getDeclaredFields();
        for(Field field:fields){
            if(field.isAnnotationPresent(Id.class)){
                primaryKeyName = field.getName();
            }
        }
    }

    public T load(Serializable id){
        return get(id);
    }

    /*不可行
    public T get(Serializable id){
        T obj = null;
        try {
         obj = entityClass.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        HashOperations<String,String,String> hashOperations = redisTemplate.opsForHash();
        Map<String,String> map = hashOperations.entries(entityClassName+id);
        try {
            //BeanUtils.populate(obj, map);
            ObjectMapper mapper = new ObjectMapper();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("hhh1234");
        }
        return obj;
    }
    */

    public T get(Serializable id){
        HashOperations<String,String,String> hashOperations = redisTemplate.opsForHash();
        Map<String,String> map = hashOperations.entries(entityClassName+id);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //T obj = mapper.convertValue(map, entityClass);
        //可行


        T obj = null;

        try {
            String json = mapper.writeValueAsString(map);
            obj = mapper.readValue(json, entityClass);
        }catch (Exception e){
            e.printStackTrace();
        }


        //T obj = JSON.parseObject(JSON.toJSONString(map),entityClass);//不可行

        //下面两个是可行的
        //T obj = ReflectionUtil.mapToBean(map,entityClass);//使用ReflectionUtil的位置有三处 get save update
        //T obj = ReflectionUtil2.mapToBean(map,entityClass);

        return obj;
    }


    /*不可行
    public void save(T entity){
        long id = redisTemplate.opsForValue().increment(entityClassName+"id",1);
        HashOperations<String,String,String> hashOperations = redisTemplate.opsForHash();
        Map<String,String> map = new HashMap<String,String>(16);
        try {
            map = BeanUtils.describe(entity);
        }catch (Exception e){
            e.printStackTrace();
        }
        hashOperations.putAll(entityClassName+id,map);
        redisTemplate.opsForSet().add(entityClassName+"all:id",id+"");
    }
    */

    public int save(T entity){
        long id = redisTemplate.opsForValue().increment(entityClassName+"id",1);
        setId(entity,Integer.parseInt(id+""));
        HashOperations<String,String,String> hashOperations = redisTemplate.opsForHash();
        Map<String,String> map = new HashMap<String,String>(16);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //map = mapper.convertValue(entity,Map.class);
        //可行

        try {
            String json = mapper.writeValueAsString(entity);
            map = mapper.readValue(json,Map.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        //map = JSON.parseObject(JSON.toJSONStringWithDateFormat(entity,"yyyy-MM-dd HH:mm:ss"),new TypeReference<Map<String,String>>() {});
        //不可行

        //下面两个是可行的
        //map = ReflectionUtil.beanToMap(entity);
        //map = ReflectionUtil2.beanToMap(entity);

        System.out.println("为了确认post"+map);

        hashOperations.putAll(entityClassName+id,map);
        redisTemplate.opsForSet().add(entityClassName+"all:id",id+"");
        return Integer.parseInt(id+"");
    }

    public List<T> loadAll(){
        Set<String> allIds = redisTemplate.opsForSet().members(entityClassName+"all:id");
        HashOperations<String,String,String> hashOperations = redisTemplate.opsForHash();
        List<T> list = new ArrayList<>(10);
        for(String id:allIds){
            list.add(get(Integer.valueOf(id)));
        }
        return removeNull(list);
    }

    public void remove(T entity){
        int id = id(entity);
        redisTemplate.opsForSet().remove(entityClassName+"all:id",id+"");
        redisTemplate.expire(entityClassName+id,0, TimeUnit.SECONDS);
    }

    public void update(T entity){
        HashOperations<String,String,String> hashOperations = redisTemplate.opsForHash();
        Map<String,String> map = new HashMap<String,String>(16);
        try {
            //map = BeanUtils.describe(entity);前面这里一直忘记注释掉
            //不可行

            //下面两个是可行的
            //map = ReflectionUtil.beanToMap(entity);
            //map = ReflectionUtil2.beanToMap(entity);

            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            //map = mapper.convertValue(entity,Map.class);
            //可行

            try {
                String json = mapper.writeValueAsString(entity);
                map = mapper.readValue(json,Map.class);
            }catch (Exception e){
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        hashOperations.putAll(entityClassName+id(entity),map);
    }

    public int id(T entity){
        Field field = null;
        try {
            field = entityClass.getDeclaredField(primaryKeyName);
            field.setAccessible(true);
            return field.getInt(entity);
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public void setId(T entity,int value){//这个方法和上一个方法对于mainPost不管用 要从父类获得再回到子类
        Field field = null;
        try {
            field = entityClass.getDeclaredField(primaryKeyName);
            field.setAccessible(true);
            field.setInt(entity,value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public <K> Long intersectAndStore(K key, Collection otherKeys, K destKey, RedisZSetCommands.Aggregate aggregate, int[] weights){
        try {
            Class<?> clazz = Class.forName("org.springframework.data.redis.core.AbstractOperations");
            Method method = clazz.getDeclaredMethod("rawKeys",Object.class,Collection.class);
            method.setAccessible(true);
            Method method1 = clazz.getDeclaredMethod("rawKey",Object.class);
            method1.setAccessible(true);
            ZSetOperations<String, String> zSetOperations = getRedisTemplate().opsForZSet();
            byte[][] rawKeys = (byte[][])method.invoke(clazz.cast(zSetOperations), key, otherKeys);
            byte[] rawDestKey = (byte[])method1.invoke(clazz.cast(zSetOperations), destKey);
            return getRedisTemplate().execute(connection -> connection.zInterStore(rawDestKey,aggregate,weights,rawKeys), true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1L;
    }

    public Page pagedQuery(String scope,int pageNo, int pageSize){
        int startIndex = Page.getStartOfPage(pageNo, pageSize);
        int endIndex = startIndex+pageSize-1;
        Long totalCount = getRedisTemplate().opsForZSet().zCard(scope);
        if(totalCount<1){
            return new Page();
        }
        Set<String> set = getRedisTemplate().opsForZSet().reverseRange(scope,startIndex,endIndex);
        List<T> list2 = new ArrayList<>(10);
        for(String id:set){
            System.out.println(id);
            list2.add(get(Integer.valueOf(id)));
            //System.out.println(get(Integer.valueOf(id)));
        }
        list2 = removeNull(list2);
        return new Page(startIndex, totalCount, pageSize, list2);
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public <T> List<T> removeNull(List<T> oldList) {

       oldList.removeAll(Collections.singleton(null));
        return oldList;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }
}
