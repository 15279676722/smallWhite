package com.example.smallwhite.io.input;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *  ByteArrayInputStream类是从内存中的字节数组中读取数据，因此它的数据源是一个字节数组。
 * @author yangqiang
 * @create 2021-09-01 16:47
 */
public class ByteArrayInputStreamTest {
    public static void main(String args[])throws IOException {

        ByteArrayOutputStream bOutput = new ByteArrayOutputStream(12);

        while( bOutput.size()!= 10 ) {
            // 获取用户输入值
            bOutput.write(System.in.read());
        }

        byte b [] = bOutput.toByteArray();
        System.out.println("Print the content");
        for(int x= 0 ; x < b.length; x++) {
            // 打印字符
            System.out.print((char)b[x]  + "   ");
        }
        System.out.println("   ");

        int c;

        ByteArrayInputStream bInput = new ByteArrayInputStream(b);

        System.out.println("Converting characters to Upper case " );
        for(int y = 0 ; y < 1; y++ ) {
            while(( c= bInput.read())!= -1) {
                System.out.println(Character.toUpperCase((char)c));
            }
            //将缓冲区重置到标记位置。 除非标记了另一个位置或在构造函数中指定了偏移量，否则标记位置为 0
            bInput.reset();
        }
    }
}
