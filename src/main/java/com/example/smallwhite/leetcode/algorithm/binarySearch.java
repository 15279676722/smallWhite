package com.example.smallwhite.leetcode.algorithm;

public class binarySearch {
    public static void main(String[] args) {
        Integer search = 91;
        Integer[] intArr = new Integer[1000];
        for (int i = 0; i < intArr.length; i++) {
            intArr[i] = i;
        }

        System.out.println("要查找的值的数组下标为---"+ binarySearch(0,intArr.length,intArr,search));
    }

    public static int binarySearch(int leftIndex,int rightIndex,Integer[] intArr,Integer searchNum){
        int centerIndex = (leftIndex + rightIndex) / 2;
        Integer centerNum = intArr[centerIndex];
        if(centerNum > searchNum){
            return binarySearch(leftIndex,centerIndex,intArr,searchNum);
        }else if(centerNum < searchNum){
            return binarySearch(centerIndex,rightIndex,intArr,searchNum);
        }else if(centerNum == searchNum){
            return centerIndex;
        }
        return 0;
    }
}
