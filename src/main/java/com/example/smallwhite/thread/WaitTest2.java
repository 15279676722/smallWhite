package com.example.smallwhite.thread;

public class WaitTest2 {



    public static void main(String[] args) {
        MyWaitThread thread1 = new MyWaitThread("thread-1");
        MyWaitThread thread2 = new MyWaitThread("thread-2");
        MyWaitThread thread3 = new MyWaitThread("thread-3");


        thread1.start();
        thread2.start();
        thread3.start();

    }

    static class MyWaitThread extends Thread {

        private static Object obj = new Object();

        private static volatile Integer count = 0;

        @Override
        public void run() {

            String name = currentThread().getName();
            while (count < 100) {

                synchronized (obj) {
                    obj.notifyAll();
                    String[] split = name.split("-");
                    if (split.length < 2) {
                        try {
                            throw new Exception("length must more than 2");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Integer index = Integer.valueOf(split[1]);

                    int remainder = (count + 1) % 3;

                    if (index%3 == remainder && count < 100) {
                        System.out.println(name + "-" + ++count);

                    }

                    if(count == 100){
                        return;
                    }
                    try {
                        obj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }


        }

        public MyWaitThread(String name) {
            super(name);
        }


    }
}
