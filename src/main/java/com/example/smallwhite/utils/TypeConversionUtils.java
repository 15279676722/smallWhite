package com.example.smallwhite.utils;


import java.sql.Timestamp;
import java.util.Date;

/**
 * 类型转换工具类
 * @description
 * @author yangqiang
 * @date   2014-4-28
 */
public class TypeConversionUtils{
    /** 字符串  **/
    private static final String TYPE_STRING_FULL = "java.lang.String";
    private static final String TYPE_STRING = "String";
    /** 整型 **/
    private static final String TYPE_INT_FULL = "java.lang.Integer";
    private static final String TYPE_INT = "Int";

    /** 时间类型  **/
    private static final String TYPE_DATE = "java.util.Date";
    /** 时间戳类型  **/
    private static final String TIMESTAMP = "java.sql.Timestamp";

    /** 浮点类型 **/
    private static final String Type_FLOAT_FULL = "java.lang.Float";
    private static final String Type_FLOAT = "float";

    /** 双精度 **/
    private static final String TYPE_DOUBLE_FULL = "java.lang.Double";
    private static final String TYPE_DOUBLE = "double";

    /** 短整型 **/
    private static final String TYPE_SHORT_FULL = "java.lang.Short";
    private static final String TYPE_SHORT = "short";

    /** 布尔类型  **/
    private static final String TYPE_BOOLEAN_FULL ="java.lang.Boolean";
    private static final String TYPE_BOOLEAN ="boolean";


    public static Object converType(String oldType,String newType,Object oldValue)
    {
        // 字符串转整型
        if( (oldType.equals(TYPE_STRING) || oldType.equals(TYPE_STRING_FULL)) &&
                (newType.equals(TYPE_INT) || newType.equals(TYPE_INT_FULL)))
        {
            return (Integer) Integer.parseInt((String)oldValue);
        }
        // 整形转字符串
        if( (oldType.equals(TYPE_INT)||oldType.equals(TYPE_INT_FULL)) &&
                (newType.equals(TYPE_STRING) || newType.equals(TYPE_STRING_FULL)))
        {
            return String.valueOf(oldValue);
        }

        // 字符串格式时间转date类型时间
        if(newType.equals(TIMESTAMP)){
            return Timestamp.valueOf((String)oldValue);
        }


        return oldValue;
    }

    /**
     * 通过对象匹配类型
     * @param obj
     * @return
     */
    public static String getType(Object obj)
    {
        if(obj instanceof Integer)
        {
            return TYPE_INT_FULL;
        }

        if(obj instanceof String)
        {
            return TYPE_STRING_FULL;
        }

        if(obj instanceof Short)
        {
            return TYPE_SHORT_FULL;
        }

        if(obj instanceof Float)
        {
            return Type_FLOAT_FULL;
        }

        if(obj instanceof Double)
        {
            return TYPE_DOUBLE_FULL;
        }

        if(obj instanceof Date)
        {
            return TYPE_DATE;
        }
        if(obj instanceof Timestamp)
        {
            return TIMESTAMP;
        }

        return TYPE_STRING_FULL;
    }

}
