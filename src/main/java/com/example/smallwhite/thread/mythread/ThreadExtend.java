package com.example.smallwhite.thread.mythread;

public class ThreadExtend extends Thread{

    public ThreadExtend(String name) {
        super(name);
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
        System.out.println("my-thread-test");
    }
}
