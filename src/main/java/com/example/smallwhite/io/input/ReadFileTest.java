package com.example.smallwhite.io.input;

import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author yangqiang
 * @create 2021-09-01 15:19
 */
public class ReadFileTest {
    @SneakyThrows
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        FileInputStream fileInputStream = new FileInputStream("/Users/yangqiang/Desktop/test.txt");
        int n = 0;
        while ((n=fileInputStream.read()) != -1){
            sb.append((char)n);
        }
        System.out.println(sb);
    }
}
