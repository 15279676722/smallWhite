package com.example.smallwhite.designpatterns.observer.V4;

import java.util.EventObject;

public class DoorEvent extends EventObject {

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DoorEvent(Object source) {
        super(source);
    }

    public DoorEvent(Object source, int state) {
        super(source);
        this.state = state;
    }
}
