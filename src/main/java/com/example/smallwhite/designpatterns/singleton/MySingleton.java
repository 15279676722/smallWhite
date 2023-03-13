package com.example.smallwhite.designpatterns.singleton;

public class MySingleton {
    private static MySingleton singleton1 = null;

    public static MySingleton getInstance(){
        if(singleton1 == null){
            return new MySingleton();
        }
        return singleton1;
    }

    private static MySingleton singleton2 = new MySingleton();

    public static MySingleton getInstance2(){
        return singleton2;
    }

    private static MySingleton singleton3 = null;

    public static MySingleton getInstance3(){
        if(singleton3 == null){
            synchronized (singleton3){
                if(singleton3 == null){
                    return new MySingleton();
                }
            }
        }
        return singleton1;
    }

    private static MySingleton singleton4 = null;

    public synchronized static MySingleton getSingleton4(){
        if(singleton4 == null){
            return new MySingleton();
        }
        return singleton4;
    }

}
