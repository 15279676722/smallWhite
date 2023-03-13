package com.example.smallwhite.java8;

import com.google.common.collect.Lists;

import io.swagger.models.auth.In;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 分批处理数据
 */
@Component
public class BatchHandlerUtils {


    public static  <T> void handler(Consumer<List<T>> consumer, List<T> batchList) {

        List<List<T>> partition = Lists.partition(batchList, 20);
        for (List<T> list : partition) {
            consumer.accept(list);
        }
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        BatchHandlerUtils.handler(System.out::println,list);
    }
}
