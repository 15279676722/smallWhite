![image-20230218162111565](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20230218162111565.png)

> jvm中的PC寄存器 也叫程序计数寄存器(Program Counter Register)中，Register的命名源于CPU的寄存器，寄存器存储指令相关的信息。CPU只有把数据装载到寄存器才能够运行。
>
> 

![image-20230218162531481](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20230218162531481.png)

 ![image-20230218162647686](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20230218162647686.png)

![image-20230218162802970](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20230218162802970.png)

PC寄存器的执行

```java
package com.example.smallwhite.jvm.chapter04;

/**
 *  javap -v
 *  执行反编译操作
 *
 * */
public class PCRegisterTest {
    public static void main(String[] args) {
        int i = 10;
        int j = 20;
        int k = i + j;
        String s = "abc";
        System.out.println(s);
    }
}
```

![image-20230218163838584](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20230218163838584.png)