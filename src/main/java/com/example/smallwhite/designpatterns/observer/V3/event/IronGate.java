package com.example.smallwhite.designpatterns.observer.V3.event;

import com.example.smallwhite.utils.LogUtil;

import java.math.BigDecimal;

public class IronGate extends AbstractUnitPriceDoor {
    public IronGate() {
        setUnitPrice(new BigDecimal(2000));
        setDoorName("铁门");
    }

    @Override
    void doPriceRise(BigDecimal price) {
        LogUtil.log("铁门的价格降了{}RMB",price);
        setUnitPrice(getUnitPrice().divide(price));
    }
}
