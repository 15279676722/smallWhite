### 堆的核心概述

![image-20230220155410329](../image/image-20230220155410329.png)

![image-20230220163956840](../image/image-20230220163956840.png)

#### 内存细分

![image-20230220164940643](../image/image-20230220164940643.png)

#### 年轻代与老年代

![image-20230220172438515](../image/image-20230220172438515.png)



![image-20230220172647973](../image/image-20230220172647973.png)

![image-20230220173107221](../image/image-20230220173107221.png)

![image-20230220174223445](../image/image-20230220174223445.png)

### 对象分配过程

![image-20230220175309687](../image/image-20230220175309687.png)

![image-20230220175321383](../image/image-20230220175321383.png)

![image-20230220175440622](../image/image-20230220175440622.png)

![image-20230220191150705](../image/image-20230220191150705.png)





#### GC

（1）Minor GC / Young GC

“新生代” 也可以称之为 “年轻代” ，这两个名词是等价的。在年轻代中的Eden内存区域被占满之后实际上就需要出发年轻代的GC，或者是新生代的GC。

此时这个新生代GC，就是所谓的 “Minor GC”，也可以称之为“Young GC”，这两个名词，都是针对新生代的GC。

（2）Full GC / Old GC

之前分析过，老年代一旦被占满之后，就会触发老年代的GC，之前称呼这种GC为Full GC。

所谓老年代的GC，称之为 “Old GC” 更加合适，因为从字面意义上就可以理解，这就是所谓的老年代GC。

但是这里把老年代GC称之为Full GC，也是可以的，只不过是一个字面意思的多种不同的说法。

（3）Full GC

针对Full GC，有个更加合适的说法，就是说Full GC指的是针对新生代、 老年代、永久代的全体内存空间的垃圾回收，所以称之为Full GC。

从字面意思理解， “Full” 就是整体的以上，所以就是对JVM进行一次整体的垃圾回收，把各个内存区域的垃圾都回收掉。

但部分人群，习惯将Full GC直接等价为 Old GC，也就是仅仅针对老年代的垃圾回收。

（4）Major GC

Major GC，一般用的比较少，也是容易混淆的概念。

有些人把Major GC跟Old GC等价起来，认为它就是针对老年代的GC，也有人把Major GC和Full GC等价起来，认为它是针对JVM全体内存区域的GC。

（5）Mixed GC

Mixed GC是G1中特有的概念，说白了，就是说在G1中，一旦老年代占据堆内存的45%了，就要出发Mixed GC，此时对年轻代和老年代都会进行回收。

![image-20230220192549735](../image/image-20230220192549735.png)

#### Minor GC / Young GC

![image-20230220193044001](../image/image-20230220193044001.png)

#### （2）Major GC/ Old GC



![image-20230220193313929](../image/image-20230220193313929.png)



#### Full GC

![image-20230220194423229](../image/image-20230220194423229.png)







![image-20230220195435576](../image/image-20230220195435576.png)









### 内存分配策略



![image-20230221111125340](../image/image-20230221111125340.png)



### TLAB

![image-20230221112233354](../image/image-20230221112233354.png)

![image-20230221112252391](../image/image-20230221112252391.png)

![image-20230221112311371](../image/image-20230221112311371.png)

![image-20230221112336711](../image/image-20230221112336711.png)



**堆是分配对象存储的唯一选择嘛**

#### 逃逸分析

![image-20230221140509581](../image/image-20230221140509581.png)

#### 栈上分配

![image-20230221140601160](../image/image-20230221140601160.png)

```java
package com.example.smallwhite.jvm.chapter08;


/**
 * -Xmx600m -Xms600m -XX:+PrintGCDetails -XX:-DoEscapeAnalysis
 *
 * */
public class StackAllocation {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        System.out.println();
        for (int i = 0; i < 10000000; i++) {
            User user = new User();
        }
        long end = System.currentTimeMillis();
        System.out.println("花费时间" + (end - start) + "ms");
    }
}

class User {

}
```

关闭逃逸分析的情况下是产生了一次GC的 并且对象都分配在堆上

```
[GC (Allocation Failure) [PSYoungGen: 153600K->1482K(179200K)] 153600K->1490K(588800K), 0.0016893 secs] [Times: user=0.01 sys=0.00, real=0.00 secs] 
花费时间103ms
Heap
 PSYoungGen      total 179200K, used 24687K [0x00000007b3800000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 153600K, 15% used [0x00000007b3800000,0x00000007b4ea9120,0x00000007bce00000)
  from space 25600K, 5% used [0x00000007bce00000,0x00000007bcf72b98,0x00000007be700000)
  to   space 25600K, 0% used [0x00000007be700000,0x00000007be700000,0x00000007c0000000)
 ParOldGen       total 409600K, used 8K [0x000000079a800000, 0x00000007b3800000, 0x00000007b3800000)
  object space 409600K, 0% used [0x000000079a800000,0x000000079a802000,0x00000007b3800000)
 Metaspace       used 3319K, capacity 4500K, committed 4864K, reserved 1056768K
  class space    used 362K, capacity 388K, committed 512K, reserved 1048576K

```

开启逃逸分析的情况没有产生GC因为对象没有分配在堆上 且运行时间明显缩短

```
花费时间8ms
Heap
 PSYoungGen      total 179200K, used 24577K [0x00000007b3800000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 153600K, 16% used [0x00000007b3800000,0x00000007b50007b0,0x00000007bce00000)
  from space 25600K, 0% used [0x00000007be700000,0x00000007be700000,0x00000007c0000000)
  to   space 25600K, 0% used [0x00000007bce00000,0x00000007bce00000,0x00000007be700000)
 ParOldGen       total 409600K, used 0K [0x000000079a800000, 0x00000007b3800000, 0x00000007b3800000)
  object space 409600K, 0% used [0x000000079a800000,0x000000079a800000,0x00000007b3800000)
 Metaspace       used 3292K, capacity 4500K, committed 4864K, reserved 1056768K
  class space    used 358K, capacity 388K, committed 512K, reserved 1048576K

```

#### 同步省略

![image-20230221144111850](../image/image-20230221144111850.png)

![image-20230221144234292](../image/image-20230221144234292.png)

> 这种同步写法当然也是有问题的





#### 分离对象或标量替换

![image-20230221145106824](../image/image-20230221145106824.png)



![image-20230221145131654](../image/image-20230221145131654.png)









![image-20230221151410835](../image/image-20230221151410835.png)

![image-20230221151528993](../image/image-20230221151528993.png)
