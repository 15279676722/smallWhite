package com.example.smallwhite.io.write;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author yangqiang
 * @create 2021-09-01 19:25
 */
public class WriteTest {
    public static void main(String[] args) {
        try {
            FileWriter fileWriter = new FileWriter("/Users/yangqiang/Desktop/test.txt");
            fileWriter.write("吃饭");
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
