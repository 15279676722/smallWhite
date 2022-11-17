package com.example.smallwhite.designpatterns.observer.V3.event;

import com.example.smallwhite.designpatterns.observer.V3.notify.AbstractObserver;
import com.example.smallwhite.designpatterns.observer.V3.notify.AbstractSubject;
import com.example.smallwhite.utils.LogUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 被观察者 门
 *
 * */

public abstract class AbstractUnitPriceDoor extends Observed {



     public BigDecimal unitPrice;

     public String doorName;

     public String getDoorName() {
          return doorName;
     }

     public void setDoorName(String doorName) {
          this.doorName = doorName;
     }

     public BigDecimal getUnitPrice() {
          return unitPrice;
     }

     public void setUnitPrice(BigDecimal unitPrice) {
          this.unitPrice = unitPrice;
     }


     public void priceRise(BigDecimal price){
          LogUtil.log("行情不好门的价格降了{}RMB",price);
          doPriceRise(price);
          AbstractSubject.notifyObserverPriceRise(price,doorName);
     }



     abstract void doPriceRise(BigDecimal price);
}
