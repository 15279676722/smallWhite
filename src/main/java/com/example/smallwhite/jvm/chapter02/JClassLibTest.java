package com.example.smallwhite.jvm.chapter02;

public class JClassLibTest {
    static class Father {
        public static int num =1;
        static {
            num =2;
        }
    }
    static class Son extends Father{
        private static int B = num;
    }

    public static void main(String[] args) {
        System.out.println(Son.B);
    }

}
