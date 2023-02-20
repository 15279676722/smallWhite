package com.example.smallwhite.jvm.chapter05;

public class StackFrameTest {
    public static void main(String[] args) {

    }

    public void method1(){
        System.out.println("method1 start");
        method2();
        System.out.println("method1 end");

    }

    public int method2(){
        System.out.println("method2 start");
        int j =20;
        int m = (int) method3();
        System.out.println("method2 end");
        return m+j;
    }


    public double method3(){
        System.out.println("method3 start");
        double i = 20.0;
        System.out.println("method3 end");
        return i;
    }
}
