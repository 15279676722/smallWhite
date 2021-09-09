package com.example.smallwhite.io.file;

import lombok.SneakyThrows;

import java.io.File;

/**
 * 删除文件
 * @author yangqiang
 * @create 2021-09-01 16:11
 */
public class DeleteFileTest {
    @SneakyThrows
    public static void main(String[] args) {
        File file = new File("/Users/yangqiang/Desktop/test.txt");
        file.deleteOnExit();
    }
}
