package com.smart.utils;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReflectionUtil {
    public static List<String> list = new ArrayList<>(10);
    public static <T> Map<String,String> beanToMap(T obj){
        Map<String,String> map = new HashMap<>(16);
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getMethods();//原来用getDeclaredMethod()是有问题的
        for(Method method:methods){
            String methodName = method.getName();
            //System.out.println(methodName);
            if(methodName.startsWith("get")&& !methodName.equals("getClass")){
                String fieldName =  methodName.substring(3,4).toLowerCase()+methodName.substring(4);
                if(method.getReturnType().equals(int.class)){
                    int returnValue = 0;
                    try{
                        returnValue = (Integer)method.invoke(obj);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    map.put(fieldName,returnValue+"");
                    continue;
                }
                if(method.getReturnType().equals(String.class)){
                    String returnValue = null;
                    try{
                        returnValue = (String)method.invoke(obj);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    map.put(fieldName,returnValue);
                    continue;
                }
                if(method.getReturnType().equals(Date.class)){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String returnValue = null;
                    try{
                        Date date = (Date)method.invoke(obj);
                        if(date!=null) {
                            returnValue = simpleDateFormat.format(date);
                        }
                        map.put(fieldName,returnValue);
                        //System.out.println(returnValue);
                    }catch (Exception e){
                        e.printStackTrace();
                        map.put(fieldName,returnValue);
                    }

                    continue;
                }
                if(list.contains(fieldName)){
                    Object o = null;
                    Class<?> clz = null;
                    try {
                        clz = method.getReturnType();
                        o = method.invoke(obj);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    map.put(fieldName+"Id",returnIdValue(clz,o)+"");//clazz cls clz cl
                }
            }
        }
        //System.out.println("bean to map");
        return map;
    }
    public static <T> T mapToBean(Map<String,String> map,Class<?> clazz){
        System.out.println("所有map "+map);
        /*
        所有map { user={password=test, lastIp=0:0:0:0:0:0:0:1, lastVisit=2019-05-18 22:49:31, userType=2, locked=0, credit=100, userName=test, userId=1},, topicId=1, replies=1, createTime=2019-05-18 22:50:13, lastPost=2019-05-18 22:50:44, digest=0, boardId=1, views=0, topicTitle=t}

         */

        if(map.isEmpty()){
            //System.out.println("is null");
            return null;
        }

        Object obj = null;
        try{
            obj = clazz.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        Method[] methods = clazz.getMethods();//原来用getDeclaredMethod()是有问题的
        for(Method method:methods){
            String methodName = method.getName();
            if(methodName.startsWith("set")) {
                String k = methodName.substring(3,4).toLowerCase()+methodName.substring(4);
                String v = map.get(k);
                    if (method.getParameterTypes()[0].equals(int.class)) {
                        if(v!=null && !v.equals("")) {
                            try {
                                method.invoke(obj, Integer.valueOf(v));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        continue;
                    }
                    if (method.getParameterTypes()[0].equals(String.class)) {
                        try {
                            method.invoke(obj, v);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        continue;
                    }
                    if (method.getParameterTypes()[0].equals(Date.class)) {
                        System.out.println("反序列化"+v);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = null;
                        if(v!=null) {
                            try {
                                if (!v.contains("CST")) {
                                    date = simpleDateFormat.parse(v);
                                } else {
                                    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                                    date = simpleDateFormat2.parse(v);
                                    System.out.println("转向新格式" + simpleDateFormat2.format(date));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            method.invoke(obj, date);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        continue;
                    }
                    if (list.contains(k)) {
                        Object o = null;
                        Class<?> clz = null;
                        try {
                            clz = method.getParameterTypes()[0];
                            o = clz.newInstance();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        int id = Integer.parseInt(map.get(k+"Id"));
                        setIdValue(clz,o,id);
                        try {
                            method.invoke(obj, clz.cast(o));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                     }
            }
        }
        return (T) obj;
    }

    public static <T> int returnIdValue(Class<?> tempClass,T obj){
        Class<?> cl = tempClass;
        List<Field> fieldList = new ArrayList<>() ;
        while (tempClass !=null && !tempClass.getName().toLowerCase().equals("java.lang.object")) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }
        for (Field f : fieldList) {
            if(f.isAnnotationPresent(Id.class)){
                String fieldName = f.getName();
                String methodName = "get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
                try {
                    Method method = cl.getMethod(methodName);
                    return (Integer)method.invoke(tempClass.cast(obj));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
       }
       return -1;
    }

    public static <T> void setIdValue(Class<?> tempClass,Object obj,int value){
        Class<?> cl = tempClass;
        List<Field> fieldList = new ArrayList<>() ;
        while (tempClass !=null && !tempClass.getName().toLowerCase().equals("java.lang.object")) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
            //这里再赋值给 Class<?> tempClass 没问题吗？
        }
        for (Field f : fieldList) {
            if(f.isAnnotationPresent(Id.class)){
                String fieldName = f.getName();
                String methodName = "set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
                try {
                    Method method = cl.getMethod(methodName,int.class);
                    method.invoke(tempClass.cast(obj),value);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
