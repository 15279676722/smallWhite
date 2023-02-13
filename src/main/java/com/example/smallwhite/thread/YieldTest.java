package com.example.smallwhite.thread;

public class YieldTest {
    public static void main(String[] args) {
        MyThread t1 = new MyThread("t1");
        MyThread t2 = new MyThread("t2");
        t1.start();
        t2.start();
    }

    static class MyThread extends Thread {


        public MyThread(String name) {
            super(name);
        }


        @Override
        public void run() {
            for (int i = 1; i < 20; i++) {
                System.out.println(currentThread().getName() + " : " + i);
                if (i % 2 == 0) {
                    System.out.println(currentThread().getName() + " : " + i + "-释放出cpu资源");
                    yield();
                    System.out.println(currentThread().getName() + " : " + i + "-拿到cpu资源");
                }
            }


        }
    }
}
