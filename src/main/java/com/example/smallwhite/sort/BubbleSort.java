package com.example.smallwhite.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BubbleSort {
    public static void main(String[] args) {
        Integer[] intArr = new Integer[100];
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            intArr[i] = random.nextInt(1000);
        }

        System.out.println(Arrays.asList(intArr));

        for (int i = 0; i < intArr.length - 1; i++) {
            for (int j = 0; j < intArr.length - 1 - i; j++) {
                if (intArr[j] > intArr[j + 1]) {
                    Integer temp = intArr[j];
                    intArr[j] = intArr[j + 1];
                    intArr[j + 1] = temp;
                }
            }

        }
        System.out.println(Arrays.asList(intArr));

    }
}
