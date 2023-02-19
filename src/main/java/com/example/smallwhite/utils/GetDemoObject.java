package com.example.smallwhite.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 *  获取随机的 vo
 * @author yangqiang
 * */
public class GetDemoObject {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GetDemoObject.class);
    /**
     *   传参 class
     *   返回 带有这个类值得 vo 目前只支持String Timestamp Integer Date
     * */
    public static <T> T getObject(Class<T> clazz) {
        T t = null;
        try {
            t = clazz.newInstance();
            Class<?> aClass = t.getClass();
            Method[] methods = aClass.getMethods();
            for (Method method : methods) {
                //为每个set方法赋值
                if ("set".equals(method.getName().substring(0, 3))) {
                    Class<?>[] clazzArray = method.getParameterTypes();
                    if (clazzArray.length != 1) {
                        continue;
                    }
                    aClass.getMethod(method.getName(), clazzArray[0]).invoke(t, RandomValue(clazzArray[0].getName()));
                }
            }
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }

    private static Object RandomValue(String type) {
        switch (type) {
            case "java.java.lang.String":
                return RandomString();
            case "java.sql.Timestamp":
                return new Timestamp(System.currentTimeMillis());
            case "java.lang.Integer":
                return (int)(Math.random()*2147483647);
            case "java.util.Date":
                return new Date();
            default:break;
        }
        LOGGER.info(type+"类型未定义自动生成VO，请添加");
        return null;
    }

    private static String RandomString() {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        //获取长度为6的字符串
        for (int i = 0; i < 20; i++) {
            //获取范围在3之内的索引值
            int number = random.nextInt(3);
            int result = 0;
            switch (number) {
                case 0:
                    //Math.random()*25+65成成65-90的int型的整型,强转小数只取整数部分
                    //对应A-Z 参考ASCII编码表
                    result = (int) (Math.random() * 25 + 65);
                    //将整型强转为char类型
                    sb.append((char) result);
                    break;
                case 1:
                    //对应a-z
                    result = (int) (Math.random() * 25 + 97);
                    sb.append((char) result);
                    break;
                case 2:
                    sb.append(new Random().nextInt(10));
                    break;
                default: break;
            }

        }
        return sb.toString();
    }


    /**
     *   传参 class index 生成集合size（）
     *   返回 带有这个类值得 vo集合 目前只支持String Timestamp Integer Date
     * */
    public static <T> List<T> getObjectList(Class<T> clazz,Integer index) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < index; i++) {
            list.add(getObject(clazz));
        }
        return list;
    }

}
