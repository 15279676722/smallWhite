package com.example.smallwhite.thread;

public class WaitTest {
    public static void main(String[] args) {
        Object a = new Object();


        new Thread(() -> {
            String threadName = Thread.currentThread().getName();
            synchronized (a){
                System.out.println(threadName+"synchronized a");
                try {
                    System.out.println(threadName+"a wait ");
                    a.wait(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(threadName+"a end ");

            }

        },"thread-1").start();

        new Thread(() -> {
            String threadName = Thread.currentThread().getName();
            synchronized (a){
                System.out.println(threadName+"synchronized a");
                try {
                    System.out.println(threadName+"a wait ");
                    a.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(threadName+"a end ");

            }

        },"thread-2").start();


        new Thread(()-> {
                String threadName = Thread.currentThread().getName();
                synchronized (a){
                    System.out.println(threadName+"synchronized a");
                    System.out.println(threadName+"a notify ");
                    a.notifyAll();
                    System.out.println(threadName+"a end ");
                }

            },"thread-3").start();
    }
}
