package com.example.smallwhite.jvm.chapter13;

import org.junit.Test;

public class StringTest {
    @Test
    public void test1() {
        //这种情况java 会优化成 String s1 = "abcdef";
        String s1 = "abc" + "def";


        //这种情况java += 会新建一个 StringBuilder对象 通过append 方法后 调用toString() 返回一个String对象
        // s2 指向的是堆中的对象地址 而不是常量池中的"abcdef"
        String s2 = "abc";
        s2 += "def";


        String s3 = "abcdef";

        System.out.println(s1 == s3); //true
        System.out.println(s1 == s2); //false
        System.out.println(s1 == s2.intern()); //true
        System.out.println(s1 == s2.toString()); //false

    }


    @Test
    public void test2() {
        String s1 = "JavaEE";
        String s2 = "hadoop";
        String s3 = "JavaEEhadoop";
        String s4 = "JavaEE" + "hadoop";
        String s5 = s1 + "hadoop";
        String s6 = "JavaEE" + s2;
        String s7 = s1 + s2;
        String s8 = s6.intern();

        System.out.println(s3 == s4);  // true 编译器优化 "JavaEE"+"hadoop"-> "JavaEEhadoop"
        System.out.println(s3 == s5);  // false 变量 + 字符串常量拼接产生新对象 对象地址指向不一样
        System.out.println(s3 == s6);  // false
        System.out.println(s3 == s7);  // false
        System.out.println(s5 == s6);  // false
        System.out.println(s5 == s7);  // false
        System.out.println(s6 == s7);  // false
        System.out.println(s3 == s8);  // true


    }

    @Test
    public void test3() {
        final String s1 = "a";
        final String s2 = "b";
        String s3 = "ab";
        String s4 = s1 + s2;
        System.out.println(s3 == s4);//true 这里s1和s2已经不是变量了。也不会使用StringBuilder去进行拼接
    }


    @Test
    public void test4() {
       String s =new String("!")+new String("2");

    }


}
