package com.example.smallwhite.io.file;

import java.io.File;

/**
 * Java 提供了 File类，它指向计算机操作系统中的文件和目录，通过该类只能访问文件和目录，无法访问内容。 它内部主要提供了 3 种操作：
 *
 * 访问文件的属性：绝对路径、相对路径、文件名······
 * 文件检测：是否文件、是否目录、文件是否存在、文件的读/写/执行权限······
 * 操作文件：创建目录、创建文件、删除文件······
 * @author yangqiang
 * @create 2021-09-01 15:45
 */
public class FileInfoTest {
    public static void main(String[] args) {
        File file = new File("/Users/yangqiang/Desktop/settings.txt");
        showFileInfo(file);
    }
    public static void showFileInfo(File file){
        //判断文件或目录是否存在
        if (file.exists()) {
            if (file.isFile()) {
                System.out.println("名称："+file.getName());
                System.out.println("相对路径："+file.getPath());
                System.out.println("绝对路径："+file.getAbsolutePath());
                System.out.println("文件大小："+file.length()+"字节");
            }
            if (file.isDirectory()) {
                System.out.println("此文件是目录");
            }
        }else {
            System.out.println("文件不存在");
        }
    }
}
