package com.example.smallwhite.thread.mythread;

import java.util.concurrent.Callable;

public class CallableImp implements Callable<String> {
    @Override
    public String call() throws Exception {
        System.out.println(Thread.currentThread().getName());
        return "my-callable-test";
    }
}
