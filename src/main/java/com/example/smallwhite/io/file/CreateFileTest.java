package com.example.smallwhite.io.file;

import lombok.SneakyThrows;

import java.io.File;

/**
 * 创建文件
 * @author yangqiang
 * @create 2021-09-01 16:11
 */
public class CreateFileTest {
    @SneakyThrows
    public static void main(String[] args) {
        File file = new File("/Users/yangqiang/Desktop", "test.txt");
        file.createNewFile();
    }
}
