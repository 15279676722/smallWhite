package com.example.smallwhite.java8;import java.util.concurrent.CompletableFuture;import java.util.concurrent.ExecutionException;import java.util.concurrent.TimeUnit;/** * 异步编程测试 * @author: yangqiang * @create: 2021-06-15 15:41 */public class CompletableFutureTest {    public static void main(String[] args) throws ExecutionException, InterruptedException {        CompletableFuture<String> future = CompletableFuture.supplyAsync(CompletableFutureTest::get);        CompletableFuture<Void> future2 = CompletableFuture.runAsync(CompletableFutureTest::get);        System.out.println("Start");        System.out.println(future.get());        System.out.println("End");        System.out.println(future2.get());    }    private static String get(){        try {            TimeUnit.SECONDS.sleep(3);        } catch (InterruptedException e) {            e.printStackTrace();        }        return "hasReturnString";    }}