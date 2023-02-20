package com.example.smallwhite.jvm.chapter08;

/**
 *
 * -Xms10m -Xmx10m
 *
 * */
public class HeapDemo {
    public static void main(String[] args) {
        System.out.println("start");
        try {
            Thread.sleep(1000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end");

    }
}
