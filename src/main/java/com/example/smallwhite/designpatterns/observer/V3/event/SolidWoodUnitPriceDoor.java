package com.example.smallwhite.designpatterns.observer.V3.event;

import com.example.smallwhite.utils.LogUtil;

import java.math.BigDecimal;

public class SolidWoodUnitPriceDoor extends AbstractUnitPriceDoor {

    public SolidWoodUnitPriceDoor() {
        setUnitPrice(new BigDecimal(1000));
        setDoorName("实木门");
    }

    @Override
    void doPriceRise(BigDecimal price) {
        LogUtil.log("实木门的价格降了{}RMB",price);
        setUnitPrice(getUnitPrice().divide(price));
    }

}
