package com.example.smallwhite.thread;

public class ProducerConsumerTest {
    private static volatile Integer count = 0;
    private static volatile Object object = new Object();

    public static void main(String[] args) {

        new Thread(() -> {
            while (true) {


                synchronized (object) {
                    while (count < 100) {
                        System.out.println("生产中..." + ++count);
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        object.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        new Thread(() -> {
            while (true) {

                synchronized (object) {
                    while (count > 50) {
                        System.out.println(Thread.currentThread().getName() + "-消费中..." + --count);
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    object.notifyAll();
                }
            }
        }).start();
        new Thread(() -> {
            while (true) {

                synchronized (object) {
                    while (count > 50) {
                        System.out.println(Thread.currentThread().getName() + "-消费中..." + --count);
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    object.notifyAll();
                }
            }
        }).start();
    }
}
