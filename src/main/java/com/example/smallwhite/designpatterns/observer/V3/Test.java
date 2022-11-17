package com.example.smallwhite.designpatterns.observer.V3;

import com.example.smallwhite.designpatterns.observer.V3.event.IronGateSwitch;
import com.example.smallwhite.designpatterns.observer.V3.event.SolidWoodUnitPriceDoor;
import com.example.smallwhite.designpatterns.observer.V3.notify.AbstractSubject;
import com.example.smallwhite.designpatterns.observer.V3.notify.ConcreteObserver;

import java.math.BigDecimal;

public class Test {
    public static void main(String[] args) {
        AbstractSubject.addObserver(new ConcreteObserver());
        SolidWoodUnitPriceDoor solidWoodDoor = new SolidWoodUnitPriceDoor();
        solidWoodDoor.priceRise(new BigDecimal(100));

        IronGateSwitch ironGateSwitch = new IronGateSwitch();
        ironGateSwitch.closeDoor();
    }
}
