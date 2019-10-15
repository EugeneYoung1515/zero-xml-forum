package com.smart.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;


//注意fastjson的配置
//1.写类名进json
//2.忽略null等
public class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private Class<T> clazz;

    public FastJson2JsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    public String serializeToString(T t) throws SerializationException {
        if (t == null) {
            System.out.println("serializeToString T t is null");
            return "";//这里这样处理合适吗
        }
        //return JSON.toJSONString(t, SerializerFeature.WriteClassName,SerializerFeature.SkipTransientField);
        return toJSONStringIgnoreTransient(t);
    }

    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);

        return (T) JSON.parseObject(str, clazz);
    }

    public T deserialize(String value) throws SerializationException{
        if(value == null){
            return null;
        }
        //byte[] valueBytes = null;
        //valueBytes = value.getBytes(DEFAULT_CHARSET);
        //return deserialize(valueBytes);

        return (T) JSON.parseObject(value, clazz);
    }

    public static String toJSONStringIgnoreTransient(Object object) {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        SerializeWriter out = new SerializeWriter();
        try {
            JSONSerializer serializer = new JSONSerializer(out);
            serializer.config(SerializerFeature.SkipTransientField, false);
            serializer.config(SerializerFeature.WriteClassName,true);
            serializer.write(object);
            return out.toString();
        } finally {
            out.close();
        }
    }
}
