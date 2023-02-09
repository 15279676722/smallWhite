package com.example.smallwhite.basics.collection;

import java.util.HashMap;

public class HashMapTest {
    public static void main(String[] args) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put(1,1);
        map.put(1,2);
        map.remove(1);
    }
}
