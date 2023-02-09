package com.example.smallwhite.designpatterns.observer.V4;

import com.example.smallwhite.utils.LogUtil;

public class OpenDoorListener implements DoorListener{
    @Override
    public void doorEvent(DoorEvent doorEvent) {
        if(doorEvent.getState() == -1){
            LogUtil.log("门关上了");
        }
    }
}
