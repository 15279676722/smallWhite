package com.example.smallwhite.designpatterns.observer.V3.event;

import com.example.smallwhite.utils.LogUtil;

public class IronGateSwitch extends AbstractSwitchDoor{
    @Override
    String doCloseDoor() {
        if(super.isOpen()){
            return "铁门关上了";
        }else {
            return "铁门已经是关上的！";
        }
    }

    @Override
    String doOpenDoor() {
        if(super.isOpen()){
            return"铁门已经是打开的！";
        }else {
            return "铁门打开了";
        }
    }
}
