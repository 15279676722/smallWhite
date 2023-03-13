package com.example.smallwhite.basics.collection;

public class MyLRU<K, V> {
    private Node<K, V> head = null;
    private Node<K, V> tail = null;
    private int capacity;
    private int size;

    public MyLRU(int capacity) {
        this.capacity = capacity;
    }

    class Node<K, V> {
        private K key;
        private V value;
        private Node<K, V> next;
        private Node<K, V> prev;

        public Node(K key, V value, Node<K, V> next, Node<K, V> prev) {
            this.key = key;
            this.value = value;
            this.next = next;
            this.prev = prev;
        }
    }

    public void put(K key, V value) {

        if (head == null) {
            head = new Node<>(key, value, null, null);

        } else if (tail == null) {
            tail = new Node<>(key, value, null, null);
            head.next = tail;
            tail.prev = head;
        } else {
            Node oldTail = tail;
            tail = new Node<>(key, value, null, oldTail);
            oldTail.next = tail;
        }
        if (++size > capacity) {
            //删除head
            Node<K, V> next = head.next;
            head = next;
            head.prev = null;
        }
    }

    public V get(K key) {
        Node<K, V> first = head;
        while (first != null) {
            if (first.key == key) {
                Node<K, V> oldTail = tail;
                Node<K, V> oldHead = head;
                Node<K, V> next = head.next;
                head = next;
                head.prev = null;
                tail = oldHead;
                tail.prev = oldTail;
                tail.next = null;
                oldTail.next = tail;
                return oldHead.value;
            }
            first = first.next;
        }
        return null;
    }

    public static void main(String[] args) {
        MyLRU<Integer, Integer> integerIntegerMyLRU = new MyLRU<>(3);
        integerIntegerMyLRU.put(1, 1);
        integerIntegerMyLRU.put(2, 2);
        integerIntegerMyLRU.put(3, 3);
        integerIntegerMyLRU.get(1);

        integerIntegerMyLRU.put(4, 4);
        integerIntegerMyLRU.get(1);
    }
}
