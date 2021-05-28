package com.example.smallwhite.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class InsertUtils {
    private static final Logger log = LoggerFactory.getLogger(InsertUtils.class.getName());

    public static <T extends Object> List<T> InsertEntity(List<T> tList) {
        for (T t : tList) {
            InsertEntity(t);
        }
        return tList;
    }

    public static <T extends Object> T InsertEntity(T t) {
        try {
            Method methodgetId = t.getClass().getMethod("getId");
            Method methodgetTs = t.getClass().getMethod("getTs");
            Method methodgetDr = t.getClass().getMethod("getDr");
            Object invokeId = methodgetId.invoke(t);
            Object invokeTs = methodgetTs.invoke(t);
            Object invokeDr = methodgetDr.invoke(t);
            if (invokeId == null) {
                String uuid = UUID.randomUUID().toString();
                t.getClass().getMethod("setId", String.class).invoke(t, uuid);
            }
            if (invokeTs == null) {
                t.getClass().getMethod("setTs", Timestamp.class).invoke(t, new Timestamp(System.currentTimeMillis()));
            }
            if (invokeDr == null) {
                t.getClass().getMethod("setDr", Integer.class).invoke(t, 0);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return t;
    }
}
