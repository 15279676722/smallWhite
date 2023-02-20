package com.example.smallwhite.jvm.chapter05;

/**
 * 默认情况下 count: 11420
 * 设置栈的大小： -Xss256k count:2465
 *
 */

public class StackOverFlowTest {
    private static int count = 0;
    public static void main(String[] args) {
        System.out.println(++count);
        main(args);
    }
}
