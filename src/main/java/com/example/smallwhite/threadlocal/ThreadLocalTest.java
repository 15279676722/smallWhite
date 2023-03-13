package com.example.smallwhite.threadlocal;

import com.example.smallwhite.thread.MyThread1;
import lombok.SneakyThrows;

public class ThreadLocalTest {
    public static void main(String[] args) {
        ThreadLocal<String> stringThreadLocal = new ThreadLocal<>();
        ThreadLocal<Integer> intThreadLocal = new ThreadLocal<>();

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    intThreadLocal.set(finalI);
                    stringThreadLocal.set("yangqiang"+ finalI);
                    System.out.println(finalI+" - "+stringThreadLocal.get()+"-"+stringThreadLocal.get() );
                }
            }).start();
        }

    }
}
