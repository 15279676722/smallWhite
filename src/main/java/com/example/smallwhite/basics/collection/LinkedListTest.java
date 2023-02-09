package com.example.smallwhite.basics.collection;

import java.util.LinkedList;
/**
 * LinkedList 是采用双向链表的方式来对元素进行存储
 * 维护了一个 first 头结点和last尾节点
 * 通过要操作的数据下标来判断要从 first还是last进行遍历数据
 *
 * */
public class LinkedListTest {
    public static void main(String[] args) {
        LinkedList<Integer> list = new LinkedList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.remove(1);
        Integer integer = list.get(1);
    }
}
