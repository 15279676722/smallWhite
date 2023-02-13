package com.example.smallwhite.thread;

public class SleepTest {
    public static void main(String[] args) throws InterruptedException {

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName()+i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"my-thread").start();

        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName()+i);
            Thread.sleep(1000);
        }

    }
}
