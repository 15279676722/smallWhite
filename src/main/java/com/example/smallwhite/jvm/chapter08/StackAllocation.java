package com.example.smallwhite.jvm.chapter08;


/**
 * -Xmx600m -Xms600m -XX:+PrintGCDetails -XX:-DoEscapeAnalysis -XX:-EliminateAllocations
 *
 * */
public class StackAllocation {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        System.out.println();
        for (int i = 0; i < 10000000; i++) {
            User user = new User();
        }
        long end = System.currentTimeMillis();
        System.out.println("花费时间" + (end - start) + "ms");
    }
}

class User {
    public int id;
    public String name;
}
