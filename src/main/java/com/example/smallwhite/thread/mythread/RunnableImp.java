package com.example.smallwhite.thread.mythread;

public class RunnableImp implements Runnable{
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
        System.out.println("my-runnable-test");
    }
}
