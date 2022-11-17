package com.example.smallwhite.designpatterns.observer.V3.notify;

import com.example.smallwhite.designpatterns.observer.V3.event.AbstractUnitPriceDoor;
import com.example.smallwhite.designpatterns.observer.V3.event.Observed;
import com.example.smallwhite.utils.LogUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Subscribe
public class ConcreteObserver<AbstractUnitPriceDoor extends Observed> extends AbstractObserver<AbstractUnitPriceDoor>{
    @Override
    public void observerPriceRise(BigDecimal price, String name) {
        LogUtil.log("{}降价了{}RMB赶紧去抢",name,price);
    }

    @Override
    public void observerCloseDoor(String status) {
        LogUtil.log(status);
    }

    @Override
    public void observerOpenDoor(String status) {
        LogUtil.log(status);
    }
}
