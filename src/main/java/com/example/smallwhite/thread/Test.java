package com.example.smallwhite.thread;


/**
 * @Auther: miv
 * @Date: 2019-09-08 16:34
 * @Web: www.xiejx.cn
 * @Email: 787824374@qq.com
 * @Description:
 */
public class Test {
    volatile int i = 1;

    public static void main(String[] args) throws Exception {
        Test obj = new Test();


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (obj.i <= 100) {
                    // 上锁当前对象
                    synchronized (this) {

                        // 唤醒另一个线程
                        notifyAll();
                        if (obj.i == 101) {
                            return;
                        }

                        int i = new Integer(Thread.currentThread().getName());
                        if (obj.i % 3 == i % 3) {
                            System.out.println("Thread " + Thread.currentThread().getName() + " " + obj.i++);
                        }
                        if (obj.i % 3 == 1) {
                            System.out.println("==========");
                        }
                        try {
                            if (obj.i == 101) {
                                notifyAll();
                                return;
                            } else {
                                // 释放掉锁
                                wait();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        // 启动多个线程（想创建几个就创建几个）
        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        Thread t3 = new Thread(runnable);

        t1.setName("1");
        t2.setName("2");
        t3.setName("3");
        t1.start();
        t2.start();
        t3.start();


    }


}



