package com.example.smallwhite.designpatterns.observer.V4;

import java.util.EventListener;

public interface DoorListener extends EventListener {
    public void doorEvent(DoorEvent event);
}
