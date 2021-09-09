package com.example.smallwhite.io.output;

import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 字符输出流
 * @author yangqiang
 * @create 2021-09-01 17:14
 */
public class FileOutputStreamTest {
    public static void main(String[] args) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/Users/yangqiang/Desktop/test.txt",true);
            String test = "吃饭了吗\nchifan";
            //字符输出流转字节流
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(test);
            outputStreamWriter.flush();
            fileOutputStream.write(test.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
