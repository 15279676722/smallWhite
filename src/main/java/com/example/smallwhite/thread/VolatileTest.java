package com.example.smallwhite.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
/**
 * volatile修饰的变量在多个线程之间是共享的
 * 但是不保证
 * */
public class VolatileTest {
    private    static Integer sum=0;
    
    public static void main(String[] args) throws InterruptedException {
        ReentrantLock reentrantLock = new ReentrantLock();

        Integer count =10000;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(() ->{
                System.out.println(++sum);
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        System.out.println(sum);
    }
}
