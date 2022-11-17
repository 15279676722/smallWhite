package com.example.smallwhite.designpatterns.observer.V3.notify;

import com.example.smallwhite.designpatterns.observer.V3.event.Observed;

import java.math.BigDecimal;

/**
 * 观察门的变更
 * */
public  class AbstractObserver<E extends Observed> {
    public  void observerPriceRise(BigDecimal price, String name){

    };

    public  void observerCloseDoor(String status){

    };

    public  void observerOpenDoor(String status){

    };

}
