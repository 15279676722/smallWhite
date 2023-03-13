package com.example.smallwhite.basics.collection;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MyCollectionTest {
    public static void main(String[] args) {
        MyCollection myCollection = new MyCollection();
        for (int i = 0; i < 100; i++) {
            myCollection.add(i);
        }
        System.out.println(myCollection);
        Iterator<Integer> iterator = myCollection.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            if (next % 2 == 0) {
                iterator.remove();
            }
        }
        System.out.println(myCollection);
    }
}