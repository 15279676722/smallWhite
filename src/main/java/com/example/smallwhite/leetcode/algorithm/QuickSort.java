package com.example.smallwhite.leetcode.algorithm;

import java.util.Arrays;
import java.util.Random;

public class QuickSort {
    public static void main(String[] args) {
        Integer[] intArr = new Integer[1000];
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            intArr[i] = random.nextInt();
        }
        System.out.println(Arrays.asList(intArr));

        quickSort(intArr);
    }

    private static void quickSort(Integer[] intArr) {
        for (int i = 0; i < intArr.length; i++) {
            Integer num = intArr[i];
            for (int j = i+1; j < intArr.length; j++) {
                Integer startIndex = intArr[j];
            }
        }
    }
}
