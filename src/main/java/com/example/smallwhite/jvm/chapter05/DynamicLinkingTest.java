package com.example.smallwhite.jvm.chapter05;

public class DynamicLinkingTest {
    int num = 10;

    public void methodA() {
        System.out.println("methodA()...");
        methodB();
    }

    public void methodB() {

        System.out.println("methodB()...");
    }
}
