

图解

https://www.processon.com/view/link/64016902d7a4e608f6e33f07

Spring 为什么需要三级缓存解决循环依赖



假设bean A B 循环依赖 并且 A 经过AOP增强处理

Spring的AOP增强操作发生在属性之后 就是这个bean对象的所有字段已经赋值完成的时候。如果只用二级缓存去存储 实例化完成的对象的话。bean A 创建过程

1.bean A 实例化完成   加入到二级缓存中

2.bean A 依赖B 实例化B 

3.bean B实例化完成 依赖bean A 从二级缓存中拿到bean A

4.bean B创建完成。bean A 属性填充完成

5.bean A 在属性填充后发生AOP增强操作 但是其bean B中的A对象并没有发生增强操作 所以就会出现问题



正常的三级缓存操作

1.bean A 实例化完成   加入到三级缓存中(三级缓存是一个ObjectFactory 如果bean A有AOP增强后续可以获得增强的对象)

2.bean A 依赖B 实例化B 

3.bean B实例化完成 依赖bean A 从三级缓存中拿到被Spring增强的bean A 并放入到二级缓存中

4.bean B创建完成。bean A 属性填充完成

5.bean A 在属性填充后发生AOP增强操作 发现其已经经过AOP增强 不再进行增强操作

6.bean A 在缓存中获取其对象发现在二级缓存中存在AOP对象 ，直接进行赋值 这样得到的对象在每次循环引用中都是增强的bean对象

