package com.smart.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.redis.FastJson2JsonRedisSerializer;
import com.smart.utils.ReflectionUtil;
import com.smart.utils.ReflectionUtil2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;

@Configuration
@ComponentScan("com.smart.redisdao")
public class RedisConfig {
    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        JedisConnectionFactory factory = new JedisConnectionFactory();
        //factory.setHostName("localhost");
        factory.setPassword("kmlbyz520");
        return factory;
    }

    @Bean
    public RedisTemplate<String,String> redisTemplate(){
        ReflectionUtil.list.add("user");
        ReflectionUtil.list.add("topic");

        ReflectionUtil2.list.add("user");
        ReflectionUtil2.list.add("topic");



        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);//这几个常量的意思是

        //mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));

        serializer.setObjectMapper(mapper);

        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(Object.class));
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer(Object.class));

        //redisTemplate.setValueSerializer(new FastJson2JsonRedisSerializer<Object>(Object.class));
        //redisTemplate.setHashValueSerializer(new FastJson2JsonRedisSerializer<Object>(Object.class));

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
