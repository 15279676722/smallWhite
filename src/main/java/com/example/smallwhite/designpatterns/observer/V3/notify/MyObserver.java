package com.example.smallwhite.designpatterns.observer.V3.notify;

import com.example.smallwhite.designpatterns.observer.V3.event.AbstractUnitPriceDoor;
import com.example.smallwhite.designpatterns.observer.V3.event.Observed;
import com.example.smallwhite.utils.LogUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;

import java.math.BigDecimal;
@Subscribe
public class MyObserver extends AbstractObserver<AbstractUnitPriceDoor>{
    @Override
    public void observerPriceRise(BigDecimal price, String name) {
        LogUtil.log("1",1);
    }

    public static void main(String[] args) {
        //spring提供的方法
        ResolvableType generic = ResolvableType.forClass(MyObserver.class).as(AbstractObserver.class).getGeneric();
        System.out.println(generic);
    }
}
