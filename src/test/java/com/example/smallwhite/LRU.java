package com.example.smallwhite;

import io.swagger.models.auth.In;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRU<K,V> {

    private static final float hashLoadFactory = 0.75f;
    private LinkedHashMap<K,V> map;
    private int cacheSize;

    public LRU(int cacheSize) {
        this.cacheSize = cacheSize;
        int capacity = (int)Math.ceil(cacheSize / hashLoadFactory) + 1;
        map = new LinkedHashMap<K,V>(capacity, hashLoadFactory, true){
            private static final long serialVersionUID = 1;

            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                if(size() > LRU.this.cacheSize){
                    System.out.println("size() > LRU.this.cacheSize");
                }
                return size() > LRU.this.cacheSize;
            }
        };
    }

    public synchronized V get(K key) {
        return map.get(key);
    }

    public synchronized void put(K key, V value) {
        map.put(key, value);
    }

    public synchronized void clear() {
        map.clear();
    }

    public synchronized int usedSize() {
        return map.size();
    }

    public void print() {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "--"+entry.getValue());
        }
        System.out.println();
    }

    public static void main(String[] args) {
        LRU<Integer, Integer> integerInLRU = new LRU<>(2);
        integerInLRU.put(1,1);
        integerInLRU.put(2,2);
        integerInLRU.print();
    }
}