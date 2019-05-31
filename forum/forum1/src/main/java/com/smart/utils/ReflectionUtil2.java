package com.smart.utils;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReflectionUtil2 {
    public static List<String> list = new ArrayList<>(10);
    public static <T> Map<String,String> beanToMap(T obj){
        Map<String,String> map = new HashMap<>(16);
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getMethods();//这里和下面从getDeclareMethod换成getMethod才对
        //原因不懂 mainPost赋给Post引用变量 上面的应该推断为Post 使用 getDeclareMethod应该是对

        //原因知道了 T推断为Post 但是getClass() 被子类重写 getClass()出来 就是MainPost
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
                    String returnValue = null;
                    try{
                        returnValue = beanToMap(method.getReturnType().cast(method.invoke(obj))).toString();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    map.put(fieldName,returnValue);
                }
            }
        }
        //System.out.println("bean to map");
        return map;
    }
    public static <T> T mapToBean(Map<String,String> map,Class<?> clazz){
        System.out.println("所有map "+map);
        Object obj = null;
        try{
            obj = clazz.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        Method[] methods = clazz.getMethods();
        for(Method method:methods){
            String methodName = method.getName();
            if(methodName.startsWith("set")) {
                String k = methodName.substring(3,4).toLowerCase()+methodName.substring(4);
                String v = map.get(k);
                try {
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
                        method.invoke(obj,date);
                        continue;
                    }
                    if (list.contains(k)) {
                        if(v==null){
                            System.out.println(k+" "+"null "+"v: "+v+" "+map);

                        }
                        Map<String, String> map2 = new HashMap<>(16);
                        String vv = getNestPOJOAsMapKV(v,map2);
                        String[] kvPairs = vv.substring(1, vv.length()-1).split(", ");
                        for (String kvPair : kvPairs) {
                            String[] kv = kvPair.split("=");
                            map2.put(kv[0], kv[1]);
                            System.out.println(kv[0]+" "+kv[1]);
                        }

                        //[\\s\\{][a-zA-Z]{1,}=\\{[\\s\\S]*\\}[,\\}]
                        //使用这个正则表达式还不够

                        //要把user部分拿出来，不展开

                        method.invoke(obj, new Object[]{mapToBean(map2, method.getParameterTypes()[0])});
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return (T) obj;
    }

    public static String getNestPOJOAsMapKV(String str,Map<String,String> map){//这个方法可能也不好
        Pattern r = Pattern.compile("[\\s\\{][a-zA-Z]{1,}=\\{[\\s\\S]*\\}[,\\}]");
        Matcher m = r.matcher(str);
        String matchValue = null;
        String returnStr = null;
        if (m.find()){
            matchValue = m.group(0);
            map.put(matchValue.substring(1,matchValue.indexOf("=")),matchValue.substring(matchValue.indexOf("=")+1,matchValue.length()-1));
            returnStr = m.replaceAll("");
            if(returnStr.endsWith(",")){
                returnStr = returnStr.substring(0,returnStr.length()-1)+"}";
            }
            if(returnStr.startsWith(" ")){
                returnStr = "{"+returnStr.substring(1,returnStr.length());
            }

            return returnStr;
        }
        return str;
    }

    //可能会有误判 如果值里含有{或者}
}
