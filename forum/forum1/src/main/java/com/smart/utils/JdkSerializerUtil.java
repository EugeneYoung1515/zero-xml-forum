package com.smart.utils;

import org.apache.shiro.codec.Base64;

import java.io.*;
import java.nio.charset.Charset;

public class JdkSerializerUtil {
    public static String objectToString(Object o){
        if(o == null){
            return null;//注意
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            byte[] bytes = baos.toByteArray();
            return Base64.encodeToString(bytes);
            //return new String(bytes, Charset.forName("UTF-8"));
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public static Object stringToObject(String s){
        //System.out.println(s);
        if(s == null){
            return null;//注意
        }
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(s));
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        }catch (IOException|ClassNotFoundException ex){
            ex.printStackTrace();
        }
        return null;
    }
}
