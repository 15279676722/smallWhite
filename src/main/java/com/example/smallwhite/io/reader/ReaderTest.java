package com.example.smallwhite.io.reader;

import lombok.SneakyThrows;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 * @author yangqiang
 * @create 2021-09-01 17:53
 */
public class ReaderTest {
    @SneakyThrows
    public static void main(String[] args) {
        FileReader fileReader = new FileReader("/Users/yangqiang/Desktop/test.txt");
        StringBuilder sb = new StringBuilder();
        int n = 0;
        while ((n=fileReader.read()) != -1){
            sb.append((char)n);
        }
        System.out.println(sb);
    }
}
