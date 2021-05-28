package com.example.smallwhite.service;

/**
 * @author: yangqiang
 * @create: 2020-04-02 20:24
 */

public class Minstrel {
    public void Before(){
        System.out.println("执行testMethod方法之前");
    }
    public void After(){
        System.out.println("执行testMethod方法之后");
    }

    public Minstrel() {

    }
}
