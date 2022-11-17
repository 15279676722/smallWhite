package com.example.smallwhite.designpatterns.observer.V3.notify;

import com.example.smallwhite.designpatterns.observer.V3.event.AbstractSwitchDoor;
import com.example.smallwhite.designpatterns.observer.V3.event.Observed;
import org.reflections.Reflections;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AbstractSubject {

    private static List<AbstractObserver> observerList = new ArrayList<>();



    static {
        Reflections reflections = new Reflections("com.example.smallwhite.designpatterns.observer.V3.notify");
        Set<Class<? extends AbstractObserver>> subTypes = reflections.getSubTypesOf(AbstractObserver.class);
        subTypes.forEach(x -> {
            try {
                initObserver(x);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });


    }

    public static void initObserver(Class<? extends AbstractObserver> clazz) throws InstantiationException, IllegalAccessException {
        Subscribe annotation = clazz.getAnnotation(Subscribe.class);
        if(annotation!=null){
            observerList.add(clazz.newInstance());
        }
    }

    public static void addObserver(AbstractObserver abstractObserver){
        if(!observerList.stream().anyMatch(item -> Objects.equals(item.getClass(), abstractObserver.getClass()))){
            observerList.add(abstractObserver);
        }
    }

    public static void removeObserver(AbstractObserver abstractObserver){
        observerList.remove(abstractObserver);
    }

    public static void notifyObserverPriceRise(BigDecimal price,String name){
        observerList.forEach(item->item.observerPriceRise(price,name));
    };

    public static void notifyObserverCloseDoor(String status){
        observerList.forEach(item->item.observerCloseDoor(status));
    };

    public static void notifyObserverOpenDoor(String status){
        observerList.forEach(item->item.observerOpenDoor(status));
    };

}
