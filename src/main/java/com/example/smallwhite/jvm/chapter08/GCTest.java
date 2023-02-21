package com.example.smallwhite.jvm.chapter08;

import java.util.ArrayList;
import java.util.List;

/**
 * -Xmx9m -Xms9m -XX:+PrintGCDetails
 *
 * */

public class GCTest {
    public static void main(String[] args) {
        int i =0;
        try {
            List<String> list = new ArrayList<>();
            String a ="testSmallWhite";
            while (true){
                list.add(a);
                a = a+"aaa";
                i ++;
            }
        }catch (Throwable e){
            e.printStackTrace();
            System.out.println("遍历次数为"+ i);
        }

    }
}
