package com.example.smallwhite.thread;

import com.example.smallwhite.thread.mythread.ThreadExtend;

public class StopThreadTest {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + "-interrupt");

            }
        });
        thread.start();
        thread.interrupt();
    }
}
