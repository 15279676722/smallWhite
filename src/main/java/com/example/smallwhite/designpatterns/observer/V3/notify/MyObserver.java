package com.example.smallwhite.designpatterns.observer.V3.notify;

import com.example.smallwhite.designpatterns.observer.V3.event.AbstractUnitPriceDoor;
import com.example.smallwhite.designpatterns.observer.V3.event.Observed;
import com.example.smallwhite.utils.LogUtil;

import java.math.BigDecimal;
@Subscribe
public class MyObserver extends AbstractObserver<AbstractUnitPriceDoor>{
    @Override
    public void observerPriceRise(BigDecimal price, String name) {
        LogUtil.log("1",1);
    }
}
