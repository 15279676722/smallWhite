package com.example.smallwhite.thread;

import lombok.SneakyThrows;

public class WaitTest2 {
    public static void main(String[] args) {
        MyWaitTest thread1 = new MyWaitTest("thread-1");
        MyWaitTest thread2 = new MyWaitTest("thread-2");
        MyWaitTest thread3 = new MyWaitTest("thread-3");


        thread1.start();
        thread2.start();
        thread3.start();

    }

    static class MyWaitTest extends Thread {
        private static Integer count = 0;

        @SneakyThrows
        @Override
        public void run() {

            String name = currentThread().getName();
            synchronized (count) {
                while (count<100){
                    String[] split = name.split("-");
                    if (split.length < 2) {
                        throw new Exception("length must more than 2");
                    }
                    Integer index = Integer.valueOf(split[1]);

                    int remainder = count + 1 % 3;

                    if (index == remainder && count < 100) {
                        System.out.println(remainder + "-" + ++count);
                        count.notifyAll();
                        count.wait();
                    }else {
                        count.wait();
                    }
                }

            }


        }

        public MyWaitTest(String name) {
            super(name);
        }


    }


}
