### 使用场景

> 在Spring的诸多应用场景中bean都是单例形式，当一个单利bean需要和一个非单利bean组合使用或者一个非单利bean和另一个非单利bean组合使用时，我们通常都是将依赖以属性的方式放到bean中来引用，然后以@Autowired来标记需要注入的属性。但是这种方式在bean的生命周期不同时将会出现很明显的问题，假设单利bean A需要一个非单利bean B（原型），我们在A中注入bean B，每次调用bean A中的方法时都会用到bean B，我们知道Spring Ioc容器只在容器初始化时执行一次，也就是bean A中的依赖bean B只有一次注入的机会，但是实际上bean B我们需要的是每次调用方法时都获取一个新的对象（原型）所以问题明显就是：我们需要bean B是一个原型bean，而事实上bean B的依赖只注入了一次变成了事实上的单例bean。

```java
package org.geekbang.thinking.in.spring.bean.test1.lookup;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class BeanB {
}
```

```java
package org.geekbang.thinking.in.spring.bean.test1.lookup;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan
public class AnnotationConfig {
}
```

```java
package org.geekbang.thinking.in.spring.bean.test1.lookup;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public class LookUpBean {
    @Lookup
    BeanB beanB(){
        return null;
    }

    public void print(){
        System.out.println(this);
        System.out.println(beanB());
    }
}
```

```java
package org.geekbang.thinking.in.spring.bean.test1.lookup;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class LookUpTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AnnotationConfig.class);
        LookUpBean bean = context.getBean(LookUpBean.class);

        for (int i = 0; i < 10; i++) {
            bean.print();
        }
    }
}
```

```
org.geekbang.thinking.in.spring.bean.test1.lookup.LookUpBean$$EnhancerBySpringCGLIB$$c04e5bec@1de5f259
org.geekbang.thinking.in.spring.bean.test1.lookup.BeanB@729d991e
org.geekbang.thinking.in.spring.bean.test1.lookup.LookUpBean$$EnhancerBySpringCGLIB$$c04e5bec@1de5f259
org.geekbang.thinking.in.spring.bean.test1.lookup.BeanB@31fa1761
org.geekbang.thinking.in.spring.bean.test1.lookup.LookUpBean$$EnhancerBySpringCGLIB$$c04e5bec@1de5f259
org.geekbang.thinking.in.spring.bean.test1.lookup.BeanB@957e06
org.geekbang.thinking.in.spring.bean.test1.lookup.LookUpBean$$EnhancerBySpringCGLIB$$c04e5bec@1de5f259
org.geekbang.thinking.in.spring.bean.test1.lookup.BeanB@32502377
org.geekbang.thinking.in.spring.bean.test1.lookup.LookUpBean$$EnhancerBySpringCGLIB$$c04e5bec@1de5f259
org.geekbang.thinking.in.spring.bean.test1.lookup.BeanB@2c1b194a
org.geekbang.thinking.in.spring.bean.test1.lookup.LookUpBean$$EnhancerBySpringCGLIB$$c04e5bec@1de5f259
org.geekbang.thinking.in.spring.bean.test1.lookup.BeanB@4dbb42b7
org.geekbang.thinking.in.spring.bean.test1.lookup.LookUpBean$$EnhancerBySpringCGLIB$$c04e5bec@1de5f259
org.geekbang.thinking.in.spring.bean.test1.lookup.BeanB@66f57048
org.geekbang.thinking.in.spring.bean.test1.lookup.LookUpBean$$EnhancerBySpringCGLIB$$c04e5bec@1de5f259
org.geekbang.thinking.in.spring.bean.test1.lookup.BeanB@550dbc7a
org.geekbang.thinking.in.spring.bean.test1.lookup.LookUpBean$$EnhancerBySpringCGLIB$$c04e5bec@1de5f259
org.geekbang.thinking.in.spring.bean.test1.lookup.BeanB@21282ed8
org.geekbang.thinking.in.spring.bean.test1.lookup.LookUpBean$$EnhancerBySpringCGLIB$$c04e5bec@1de5f259
org.geekbang.thinking.in.spring.bean.test1.lookup.BeanB@36916eb0
```

> 可以看出来A是单例对象 而B对象每次都是一个新的bean



### 源码分析

#### AutowiredAnnotationBeanPostProcessor#determineCandidateConstructors

在选择构造函数时会去判断 bean是否会有@Lookup注解的方法 如果找到了会放入`lookupMethodsChecked`

```java
public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, final String beanName)
      throws BeanCreationException {
   //处理含有@Lookup注解的方法
   if (!this.lookupMethodsChecked.contains(beanName)) {
      // 判断该类是否有LookUp 注解
      if (AnnotationUtils.isCandidateClass(beanClass, Lookup.class)) {
         try {
            Class<?> targetClass = beanClass;
            do {
               // 遍历该类下的所有方法 找不到再从父类找
               ReflectionUtils.doWithLocalMethods(targetClass, method -> {
                  //找到带LookUp注解的方法
                  Lookup lookup = method.getAnnotation(Lookup.class);
                  if (lookup != null) {
                     Assert.state(this.beanFactory != null, "No BeanFactory available");
                     LookupOverride override = new LookupOverride(method, lookup.value());
                     try {
                        RootBeanDefinition mbd = (RootBeanDefinition)
                              this.beanFactory.getMergedBeanDefinition(beanName);
                        // override 加入beanDefinition中
                        mbd.getMethodOverrides().addOverride(override);
                     }
                     catch (NoSuchBeanDefinitionException ex) {
                        throw new BeanCreationException(beanName,
                              "Cannot apply @Lookup to beans without corresponding bean definition");
                     }
                  }
               });
               targetClass = targetClass.getSuperclass();
            }
            while (targetClass != null && targetClass != Object.class);

         }
         catch (IllegalStateException ex) {
            throw new BeanCreationException(beanName, "Lookup method resolution failed", ex);
         }
      }
      /**
       * 无论对象中是否含有@Lookup方法，过滤完成后都会放到集合中，证明此Bean已经检查完@Lookup注解了
       */
      this.lookupMethodsChecked.add(beanName);
   }
   }
```

#### SimpleInstantiationStrategy#instantiate

后续实例化对象的时候会去判断`lookupMethodsChecked` 是否有值 如果没有的话使用普通的实例对象

如果有@LookUp 注解 则使用 Cglib

```java
public Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
   // Don't override the class with CGLIB if no overrides.
   if (!bd.hasMethodOverrides()) {
      Constructor<?> constructorToUse;
      synchronized (bd.constructorArgumentLock) {
         constructorToUse = (Constructor<?>) bd.resolvedConstructorOrFactoryMethod;
         if (constructorToUse == null) {
            final Class<?> clazz = bd.getBeanClass();
            if (clazz.isInterface()) {
               throw new BeanInstantiationException(clazz, "Specified class is an interface");
            }
            try {
               if (System.getSecurityManager() != null) {
                  constructorToUse = AccessController.doPrivileged(
                        (PrivilegedExceptionAction<Constructor<?>>) clazz::getDeclaredConstructor);
               }
               else {
                  constructorToUse = clazz.getDeclaredConstructor();
               }
               bd.resolvedConstructorOrFactoryMethod = constructorToUse;
            }
            catch (Throwable ex) {
               throw new BeanInstantiationException(clazz, "No default constructor found", ex);
            }
         }
      }
      return BeanUtils.instantiateClass(constructorToUse);
   }
   else {
      // Must generate CGLIB subclass.
      return instantiateWithMethodInjection(bd, beanName, owner);
   }
}
```



```java
private Class<?> createEnhancedSubclass(RootBeanDefinition beanDefinition) {
   Enhancer enhancer = new Enhancer();
   enhancer.setSuperclass(beanDefinition.getBeanClass());
   enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
   if (this.owner instanceof ConfigurableBeanFactory) {
      ClassLoader cl = ((ConfigurableBeanFactory) this.owner).getBeanClassLoader();
      enhancer.setStrategy(new ClassLoaderAwareGeneratorStrategy(cl));
   }
   enhancer.setCallbackFilter(new MethodOverrideCallbackFilter(beanDefinition));
   enhancer.setCallbackTypes(CALLBACK_TYPES);
   return enhancer.createClass();
}
```