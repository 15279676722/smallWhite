package com.example.smallwhite.designpatterns.observer.V4;

public class EventTest {
    public static void main(String[] args) {
        EventSource eventSource = new EventSource();
        eventSource.addListener(new CloseDoorListener());
        eventSource.addListener(new OpenDoorListener());
        eventSource.notifyListenerEvents(new DoorEvent("关门事件", -1));
        eventSource.notifyListenerEvents(new DoorEvent("开门时间", 1));
    }
}
