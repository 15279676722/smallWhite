package com.example.smallwhite.designpatterns.observer.V4;

import java.util.Vector;

public class EventSource {
    private Vector<DoorListener> listenerList = new Vector<>();
    public void addListener(DoorListener eventListener) {
        listenerList.add(eventListener);
    }
    public void removeListener(DoorListener eventListener) {
        listenerList.remove(eventListener);
    }
    public void notifyListenerEvents(DoorEvent event) {
        for (DoorListener eventListener : listenerList) {
            eventListener.doorEvent(event);
        }
    }

}
