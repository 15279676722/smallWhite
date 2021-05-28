package com.example.smallwhite.service;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author: yangqiang
 * @create: 2020-04-02 19:51
 */
public class TestClass {
    public TestClass( ) {
    }
    @Transactional
    public void test(){
        System.out.println("执行test方法");
    }
}
