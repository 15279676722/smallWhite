package com.example.smallwhite.thread;

import java.util.concurrent.atomic.AtomicInteger;

public class ProducerConsumerTest2 {
    private static volatile AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                while (count.get() < 100) {
                    System.out.println("生产中..." + count.incrementAndGet());
                    try {
                        Thread.sleep(40L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                while (count.get() > 50) {
                    System.out.println(Thread.currentThread().getName() + "-消费中..." + count.decrementAndGet());
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        new Thread(() -> {
            while (true) {
                while (count.get() > 50) {
                    System.out.println(Thread.currentThread().getName() + "-消费中..." + count.decrementAndGet());
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();
    }
}
