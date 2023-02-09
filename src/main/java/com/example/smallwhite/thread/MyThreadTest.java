package com.example.smallwhite.thread;

public class MyThreadTest {
    public static void main(String[] args) {
        Thread thread = new MyThread();
        thread.start();
    }

    static class MyThread extends Thread{
        @Override
        public void run() {
            System.out.println(1);
        }
    }
    class MyRunnable implements Runnable
}
