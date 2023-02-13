package com.example.smallwhite.thread.mythread;

public class ThreadExtend extends Thread{

    public ThreadExtend(String name) {
        super(name);
    }

    public ThreadExtend() {
    }

    @Override
    public void run() {
        super.run();
        System.out.println(Thread.currentThread().getName());
    }
}
