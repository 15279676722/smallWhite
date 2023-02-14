package com.example.smallwhite.thread;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadLocalTest {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10, new NamedThreadFactory("thread-local"));

        for (int i = 0; i < 10; i++) {
            executorService.execute(new MyRunnable(i));
        }
        executorService.shutdown();
    }


    @Data
    static
    class MyRunnable implements Runnable {

        private ThreadLocal<String> threadLocal = ThreadLocal.withInitial(() -> "chem");


        private Integer count;

        public MyRunnable(Integer count) {
            this.count = count;
        }

        @Override
        public void run() {
            threadLocal.set(count + "");
            System.out.println(Thread.currentThread().getName() + "-" + threadLocal.get());
        }
    }

    static class NamedThreadFactory implements ThreadFactory {

        private AtomicInteger threadNumber = new AtomicInteger(0);
        private String prefix;

        public NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, prefix + threadNumber.getAndIncrement());
        }
    }
}
