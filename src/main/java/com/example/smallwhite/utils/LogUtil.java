package com.example.smallwhite.utils;

import com.example.smallwhite.designpatterns.observer.V2.MessageEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
     public static void log(String var1, Object... var2){
         Logger logger = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
         logger.info(var1,var2);
     }
}
