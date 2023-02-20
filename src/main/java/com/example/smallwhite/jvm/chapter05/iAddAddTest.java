package com.example.smallwhite.jvm.chapter05;

public class iAddAddTest {
    public static void main(String[] args) {
        int i1 = 1;
        int i2 = 1;
        i1 = i1++;
        i2 = ++i2;
        System.out.println(i1);
        System.out.println(i2);
    }
}
