package com.example.smallwhite.service;

import org.springframework.stereotype.Service;

/**
 * @author: yangqiang
 * @create: 2020-04-03 11:10
 */
@Service
public class TestInterfaceImpl implements TestInterface{
    @Override
    public void testMethod() {
        System.out.println("执行testMethod方法！");
    }
}
