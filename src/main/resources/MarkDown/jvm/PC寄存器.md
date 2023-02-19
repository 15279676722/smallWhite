![image-20230218162111565.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/29a5b48a80cb40be82202c9c350ca3fb~tplv-k3u1fbpfcp-zoom-in-crop-mark:4536:0:0:0.awebp?)

> jvm中的PC寄存器 也叫程序计数寄存器(Program Counter Register)中，Register的命名源于CPU的寄存器，寄存器存储指令相关的信息。CPU只有把数据装载到寄存器才能够运行。
>

![image-20230218162531481.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/decf32e73e7b422eb01bc7c2f4f77f14~tplv-k3u1fbpfcp-zoom-in-crop-mark:4536:0:0:0.awebp?)

![image-20230218162802970.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3975d2170baa4275a6ae0c44f1a64bb5~tplv-k3u1fbpfcp-zoom-in-crop-mark:4536:0:0:0.awebp?)

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

![image-20230218163838584.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/36d05bf56aed49679a3b6934680319a9~tplv-k3u1fbpfcp-zoom-in-crop-mark:4536:0:0:0.awebp?)

