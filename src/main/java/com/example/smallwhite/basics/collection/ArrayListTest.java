package com.example.smallwhite.basics.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ArrayList 用来存储数据的格式 是 数组
 * Array获取数据的时间复杂度是O(1)
 * 有很好的遍历性能
 * 但是删除操作的开销很大 因为需要重排数组中的元素
 *
 * add() 方法
 *    1.考虑是否要进行扩容 每次扩容当前容量的 1/2
 *    2.直接将 array[size] 赋值为当前元素 size再加一。
 * remove() 方法
 *    1.判断移除的是否为末尾元素
 *    2.不是末尾元素需要做 元素的重排
 *    3.array[size-1] 置为null size 再减一
 * get() 方法
 *    1.取当前数组array[i] 值
 *
 * ArrayList 的三种循环删除元素的方式
 *    1. for i 不会进行报错
 *          但是可能会出现有的元素未被删除 因为每一次删除操作后都会有一次元素重排 导致后面的元素前移
 *    2. forEach 会进行报错
 *          forEach内部会调用 iterator操作 iterator内部维护了一个 expectedModCount 操作次数。
 *          如果不是通过Iterator的remove方法取调用的话 expectedModCount不会更新 expectedModCount!=modCount 就会报错
 *    3. Iterator 不会进行报错
 *          注意 每次调用next() 使得游标右移
 *          Iterator的remove方法会对expectedModCount进行更新
 *
 * 和LinkedList的区别
 *     遍历和修改速度: ArrayList>LinkedList
 *     删除速度: ArrayList<LinkedList
 *     插入速度:
 *             尾部插入:在尾部插入数据，数据量较小时LinkedList比较快，
 *             因为ArrayList要频繁扩容，当数据量大时ArrayList比较快，
 *             因为ArrayList扩容是当前容量*1.5，大容量扩容一次就能提供很多空间，当ArrayList不需扩容时效率明显比LinkedList高，因为直接数组元素赋值不需new Node
 *
 *             首部插入:在首部插入数据，LinkedList较快，因为LinkedList遍历插入位置花费时间很小，而ArrayList需要将原数组所有元素进行一次System.arraycopy
 *
 *             插入位置越往中间，LinkedList效率越低，因为它遍历获取插入位置是从两头往中间搜，index越往中间遍历越久，因此ArrayList的插入效率可能会比LinkedList高
 *
 *             插入位置越往后，ArrayList效率越高，因为数组需要复制后移的数据少了，那么System.arraycopy就快了，因此在首部插入数据LinkedList效率比ArrayList高，尾部插入数据ArrayList效率比LinkedList高
 * */
public class ArrayListTest {
    public static void main(String[] args) {

        List<String> list = new ArrayList<>();
        list.add("test_01");
        list.add("test_02");
        list.add("test_03");
//        for (int i = 0; i < list.size(); i++) {
//            System.out.println(list.remove(list.get(i)));//并发修改异常
//        }
//        for(String l:list){
//            System.out.println(list.remove(l));//并发修改异常
//        }
        Iterator<String> iterator = list.iterator();
        System.out.println("Iterator 删除前 数据"+list.toString());

        while (iterator.hasNext()) {
            System.out.println("Iterator 删除" + iterator.next());
            iterator.remove();
        }



    }
}
