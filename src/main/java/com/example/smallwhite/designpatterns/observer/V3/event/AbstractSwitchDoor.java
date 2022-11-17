package com.example.smallwhite.designpatterns.observer.V3.event;

import com.example.smallwhite.designpatterns.observer.V3.notify.AbstractSubject;
import com.example.smallwhite.utils.LogUtil;

import java.math.BigDecimal;

/**
 * 被观察者 门
 *
 * */

public abstract class AbstractSwitchDoor extends Observed {



     public boolean isOpen;

     public boolean isOpen() {
          return isOpen;
     }

     public void setOpen(boolean open) {
          isOpen = open;
     }

     public void closeDoor(){
          String status = doCloseDoor();
          AbstractSubject.notifyObserverCloseDoor(status);
     }

     public void openDoor(){
          String status = doOpenDoor();
          AbstractSubject.notifyObserverOpenDoor(status);
     }



     abstract String doCloseDoor();
     abstract String doOpenDoor();

}
