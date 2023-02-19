package com.example.smallwhite.jvm.chapter04;

/**
 *  javap -v
 *  执行反编译操作
 *
 * */
public class PCRegisterTest {
    public static void main(String[] args) {
        int i = 10;
        int j = 20;
        int k = i + j;
        String s = "abc";
        System.out.println(s);
    }
}
