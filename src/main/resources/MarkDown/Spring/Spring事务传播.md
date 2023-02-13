### Spring事务的传播级别有以下几种

| 级别名称                  | 作用                                                         |      |
| ------------------------- | ------------------------------------------------------------ | ---- |
| PROPAGATION_REQUIRED      | 如果当前存在事务就加入 不存在就新建一个事务 这样保证整个执行过程都是同一个事务只要一个方法回滚，整个事务均回滚。 |      |
| PROPAGATION_SUPPORTS      | 支持当前事务，如果当前没有事务，就以非事务方式执行。         |      |
| PROPAGATION_MANDATORY     | 支持当前事务，如果当前没有事务，就抛出异常。                 |      |
| PROPAGATION_REQUIRES_NEW  | 创建一个新的事务 如果当前存在事务 则把当前事务挂起  也就是说不管外部方法是否开启事务，Propagation.REQUIRES_NEW修饰的内部方法会新开启自己的事务， 且开启的事务相互独立，互不干扰。 |      |
| PROPAGATION_NOT_SUPPORTED | 以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。   |      |
| PROPAGATION_NEVER         | 以非事务方式执行，如果当前存在事务，则抛出异常。             |      |
| PROPAGATION_NESTED        | 如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则新增一个 |      |



来个简单的实例研究一下

### accountBalance和 transactionalFail都是Propagation.REQUIRED 传播级别 以下简称AB

#### 测试代码

```java
package org.geekbang.thinking.in.spring.aop.features.transactional.service.impl;


import org.geekbang.thinking.in.spring.aop.features.transactional.dao.AccountDao;
import org.geekbang.thinking.in.spring.aop.features.transactional.service.AccountService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;

@Service
public class AccountServiceAnnoFailImpl implements AccountService {
    @Autowired
    AccountDao accountDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public double accountBalance(int lessenId, int addId, double balance) throws IOException {

        //某个账号减少金额
        accountDao.lessenBalance(lessenId, balance);

        //走个账号增加金额
        accountDao.addBalance(addId, balance);

        System.out.println(TransactionSynchronizationManager.getCurrentTransactionName());


        AccountServiceAnnoFailImpl accountServiceAnnoFail =                                 (AccountServiceAnnoFailImpl)AopContext.currentProxy();
        accountServiceAnnoFail.transactionalFail(lessenId, addId, balance);

        //模拟出现异常
//		int a=5/0;
        //IOException不会进行事务的回滚 只会回滚RunTimeException和Error 需要回滚可以加上参数rollbackFor
//        throw new Exception();

        return balance;
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void transactionalFail(int lessenId, int addId, double balance) throws IOException {
        System.out.println(TransactionSynchronizationManager.getCurrentTransactionName());

        //某个账号减少金额
        accountDao.lessenBalance(lessenId, balance, "account_new");

        //走个账号增加金额
        accountDao.addBalance(addId, balance, "account_new");
        throw new IOException();
    }

}
 
```

```java
package org.geekbang.thinking.in.spring.aop.features.transactional;

import org.geekbang.thinking.in.spring.aop.features.condition.MyCondition;
import org.geekbang.thinking.in.spring.aop.features.transactional.bean.TransactionalImpl;
import org.geekbang.thinking.in.spring.aop.features.transactional.service.AccountService;
import org.geekbang.thinking.in.spring.aop.features.transactional.service.impl.AccountServiceAnnoFailImpl;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Map;

@EnableTransactionManagement
@EnableAspectJAutoProxy(exposeProxy = true)
@Configuration
@ComponentScan(value = "org.geekbang.thinking.in.spring.aop.features.transactional"
)
public class TransactionalDemo {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(TransactionalDemo.class);
        context.refresh();
        AccountService accService = context.getBean("accountServiceAnnoFailImpl", AccountService.class);
        accService.accountBalance(1, 2, 500);
//        applyTransactional(beansOfType);
        context.close();
    }

    private static void applyTransactional(Map<String, AccountService> beansOfType) throws Exception {
        for(String key:beansOfType.keySet()){
            AccountService accountService = beansOfType.get(key);
            if (accountService instanceof AccountServiceAnnoFailImpl) {
                accountService.accountBalance(1, 2, 500);
            }
        }

    }


    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
        return new TransactionTemplate(platformTransactionManager);
    }

    @Bean
    @Conditional(value = MyCondition.class)
    public PlatformTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean("dataSource")
    public DriverManagerDataSource driverManagerDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/spring_jdbc");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }


}

```

#### 测试

#####  1.两个方法事务传播级别都是Propagation.REQUIRED 

> 1.A发生异常时 B正常执行
>
> 2.A发生异常时 B发生异常
>
> 3.A正常执行时 B正常执行
>
> 4.A正常执行时 B发生异常

##### 测试结果

>1.两个方法对应的事务同时进行了回滚
>
>2.两个方法对应的事务同时进行了回滚
>
>3.两个方法对应的事务同时提交
>
>4.两个方法对应的事务同时进行了回滚

##### 结论

两个方法的事务传播级别都是Propagation.REQUIRED 此时两个方法对应的事务也是同一个 所以其中任何一个方法报错了都会进行回滚。即使是B方法报错 在 A方法中catch住了也是会进行回滚操作的。由事务的传播级别来看可知 方法A的传播级别是Propagation.REQUIRED 方法B的传播级别是PROPAGATION_SUPPORTS或者PROPAGATION_MANDATORY与测试1的结果是一致的因为都是支持当前的事务 所以就不做测试了



#####  2.方法A的传播级别是Propagation.REQUIRED 方法B的传播级别是PROPAGATION_REQUIRES_NEW

> 1.A发生异常时 B正常执行
>
> 2.A发生异常时 B发生异常
>
> 3.A正常执行时 B正常执行
>
> 4.A正常执行时 B发生异常
>
> 5.A正常执行时 B发生异常 A catch住了B的异常

##### 测试结果

>1.B事务提交，A事务回滚
>
>2.B事务回滚，A事务回滚
>
>3.B事务提交，A事务提交
>
>4.B事务回滚，A事务回滚
>
>5.B事务回滚，A事务提交

##### 结论

A方法的事务传播级别是Propagation.REQUIRED 

B方法的事务传播级别是PROPAGATION_REQUIRES_NEW

这是在两个事务之间的操作。只要异常从B外溢到A中的时候 A进行了处理。那事务B中发生的异常就不会影响到事务A



#####  3.方法A的传播级别是Propagation.REQUIRED 方法B的传播级别是PROPAGATION_NOT_SUPPORTED

> 1.A发生异常时 B正常执行
>
> 2.A发生异常时 B发生异常
>
> 3.A正常执行时 B正常执行
>
> 4.A正常执行时 B发生异常
>
> 5.A正常执行时 B发生异常 A catch住了B的异常

##### 测试结果

>1.首先B方法不以事务的方式去执行 所以也不存在B事务一说。B方法的数据库操作正常保存，A事务回滚
>
>2.B方法的数据库操作正常保存，A事务回滚
>
>3.B方法的数据库操作正常保存，A事务提交
>
>4.B方法的数据库操作正常保存，异常外溢导致A事务回滚
>
>5.B方法的数据库操作正常保存，A事务提交

##### 结论

A方法的事务传播级别是Propagation.REQUIRED 

B方法的事务传播级别是PROPAGATION_NOT_SUPPORTED

B方法从始至终就不会以一个事务的方式去执行方法操作 所以B方法内部不存在事务操作

唯一能影响到A方法事务的因素是B方法发生异常后外溢到A方法对应的事务上





#####  4.方法A的传播级别是Propagation.REQUIRED 方法B的传播级别是PROPAGATION_NESTED

> 1.A发生异常时 B正常执行
>
> 2.A发生异常时 B发生异常
>
> 3.A正常执行时 B正常执行
>
> 4.A正常执行时 B发生异常
>
> 5.A正常执行时 B发生异常 A catch住了B的异常

##### 测试结果

>1.A与B一起回滚 
>
>2.B先回滚，A再回滚
>
>3.A与B一起提交
>
>4.B先回滚，A再回滚
>
>5.B先回滚，A再正常提交

##### 结论

A方法的事务传播级别是Propagation.REQUIRED 

B方法的事务传播级别是PROPAGATION_NESTED

B方法对应的事务是A方法的一个嵌套事务  B事务创建的时候 就存在了一个保存点(SavePoint) 方便主事务随时回退到这个点

B事务的提交由A事务来进行  但是B事务的回退操作可以自己进行 回退到保存点



#### 源码分析

##### TransactionInterceptor

Spriing 事务是通过AOP的方式来进行操作的 其中的TransactionInterceptor 来保证调用的方法在事务中进行
保证事务的开启 回滚和提交

```java
package org.springframework.transaction.interceptor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;


public class TransactionInterceptor extends TransactionAspectSupport implements MethodInterceptor, Serializable {


   public TransactionInterceptor() {
   }


   public TransactionInterceptor(PlatformTransactionManager ptm, Properties attributes) {
      setTransactionManager(ptm);
      setTransactionAttributes(attributes);
   }


   public TransactionInterceptor(PlatformTransactionManager ptm, TransactionAttributeSource tas) {
      setTransactionManager(ptm);
      setTransactionAttributeSource(tas);
   }


   @Override
   @Nullable
   public Object invoke(MethodInvocation invocation) throws Throwable {
      // Work out the target class: may be {@code null}.
      // The TransactionAttributeSource should be passed the target class
      // as well as the method, which may be from an interface.
      // 首先这里是需要获取 targetClass， 如果是代理类(如 CglibProxy或者 jdkProyx),那就 获取对应的 target class
      Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);

      // Adapt to TransactionAspectSupport's invokeWithinTransaction...
      return invokeWithinTransaction(invocation.getMethod(), targetClass, invocation::proceed);
   }


   //---------------------------------------------------------------------
   // Serialization support
   //---------------------------------------------------------------------

   private void writeObject(ObjectOutputStream oos) throws IOException {
      // Rely on default serialization, although this class itself doesn't carry state anyway...
      oos.defaultWriteObject();

      // Deserialize superclass fields.
      oos.writeObject(getTransactionManagerBeanName());
      oos.writeObject(getTransactionManager());
      oos.writeObject(getTransactionAttributeSource());
      oos.writeObject(getBeanFactory());
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      // Rely on default serialization, although this class itself doesn't carry state anyway...
      ois.defaultReadObject();

      // Serialize all relevant superclass fields.
      // Superclass can't implement Serializable because it also serves as base class
      // for AspectJ aspects (which are not allowed to implement Serializable)!
      setTransactionManagerBeanName((String) ois.readObject());
      setTransactionManager((PlatformTransactionManager) ois.readObject());
      setTransactionAttributeSource((TransactionAttributeSource) ois.readObject());
      setBeanFactory((BeanFactory) ois.readObject());
   }

}
```

##### TransactionAspectSupport

TransactionAspectSupport事务支持类

![image-20221217185609578](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20221217185609578.png)

TransactionAspectSupport实现了InitializingBean 和BeanFactoryAware 这两个接口

```java
@Override
public void setBeanFactory(@Nullable BeanFactory beanFactory) {
	this.beanFactory = beanFactory;
}

@Override
public void afterPropertiesSet() {
   //首先会判断有没有配置对应的事务管理器 并且判断beanFactory是否为null
   if (getTransactionManager() == null && this.beanFactory == null) {
      throw new IllegalStateException(
            "Set the 'transactionManager' property or make sure to run within a BeanFactory " +
            "containing a TransactionManager bean!");
   }
   //判断transactionAttributeSource 是否为空
   if (getTransactionAttributeSource() == null) {
      throw new IllegalStateException(
            "Either 'transactionAttributeSource' or 'transactionAttributes' is required: " +
            "If there are no transactional methods, then don't use a transaction aspect.");
   }
}
```

接下来就是他的具体的操作事务的方法

```java
@Nullable
protected Object invokeWithinTransaction(Method method, @Nullable Class<?> targetClass,
                               final InvocationCallback invocation) throws Throwable {

   //获取事务属性，如果 transaction attribute 为null, 那这个方法就是 非事务类型
   TransactionAttributeSource tas = getTransactionAttributeSource();
   final TransactionAttribute txAttr = (tas != null ? tas.getTransactionAttribute(method, targetClass) : null);
   // 根据事务属性确定对应的事务
   final TransactionManager tm = determineTransactionManager(txAttr);
   // 这里是反应式事务,暂时不分析
   if (this.reactiveAdapterRegistry != null && tm instanceof ReactiveTransactionManager) {
      boolean isSuspendingFunction = KotlinDetector.isSuspendingFunction(method);
      boolean hasSuspendingFlowReturnType = isSuspendingFunction &&
            COROUTINES_FLOW_CLASS_NAME.equals(new MethodParameter(method, -1).getParameterType().getName());
      if (isSuspendingFunction && !(invocation instanceof CoroutinesInvocationCallback)) {
         throw new IllegalStateException("Coroutines invocation not supported: " + method);
      }
      CoroutinesInvocationCallback corInv = (isSuspendingFunction ? (CoroutinesInvocationCallback) invocation : null);

      ReactiveTransactionSupport txSupport = this.transactionSupportCache.computeIfAbsent(method, key -> {
         Class<?> reactiveType =
               (isSuspendingFunction ? (hasSuspendingFlowReturnType ? Flux.class : Mono.class) : method.getReturnType());
         ReactiveAdapter adapter = this.reactiveAdapterRegistry.getAdapter(reactiveType);
         if (adapter == null) {
            throw new IllegalStateException("Cannot apply reactive transaction to non-reactive return type: " +
                  method.getReturnType());
         }
         return new ReactiveTransactionSupport(adapter);
      });

      InvocationCallback callback = invocation;
      if (corInv != null) {
         callback = () -> CoroutinesUtils.invokeSuspendingFunction(method, corInv.getTarget(), corInv.getArguments());
      }
      Object result = txSupport.invokeWithinTransaction(method, targetClass, callback, txAttr, (ReactiveTransactionManager) tm);
      if (corInv != null) {
         Publisher<?> pr = (Publisher<?>) result;
         return (hasSuspendingFlowReturnType ? KotlinDelegate.asFlow(pr) :
               KotlinDelegate.awaitSingleOrNull(pr, corInv.getContinuation()));
      }
      return result;
   }

   PlatformTransactionManager ptm = asPlatformTransactionManager(tm);

   // 获取方法唯一标识，这里的 descriptor 就是在 获取 事务属性txAttr时 设置进去的.
   final String joinpointIdentification = methodIdentification(method, targetClass, txAttr);
   // 我们一般声明式事务定义的是DataSourceTransactionManager就不是CallbackPreferringPlatformTransactionManager，CallbackPreferringPlatformTransactionManager 是通过回调方法实现事务的
   //声明式事务
   if (txAttr == null || !(ptm instanceof CallbackPreferringPlatformTransactionManager)) {
      // Standard transaction demarcation with getTransaction and commit/rollback calls.
      // 有必要时创建一个事务
      TransactionInfo txInfo = createTransactionIfNecessary(ptm, txAttr, joinpointIdentification);

      Object retVal;
      //这个相当于是一个around advice
      // invocation.proceedWithInvocation() 进行方法的执行
      // completeTransactionAfterThrowing(txInfo, ex); 进行异常的回滚 或者 非拦截的异常的事务的提交
      // cleanupTransactionInfo 还原线程状态
      try {
         // This is an around advice: Invoke the next interceptor in the chain.
         // This will normally result in a target object being invoked.
         retVal = invocation.proceedWithInvocation();
      } catch (Throwable ex) {
         // 异常事务回滚 或者非指定异常事务提交
         completeTransactionAfterThrowing(txInfo, ex);
         throw ex;
      } finally {
         cleanupTransactionInfo(txInfo);
      }

      if (retVal != null && vavrPresent && VavrDelegate.isVavrTry(retVal)) {
         // Set rollback-only in case of Vavr failure matching our rollback rules...
         TransactionStatus status = txInfo.getTransactionStatus();
         if (status != null && txAttr != null) {
            retVal = VavrDelegate.evaluateTryFailure(retVal, txAttr, status);
         }
      }
           //提交事务
      commitTransactionAfterReturning(txInfo);
      return retVal;
   }
   //编程式事务
   else {
      Object result;
      final ThrowableHolder throwableHolder = new ThrowableHolder();

      // It's a CallbackPreferringPlatformTransactionManager: pass a TransactionCallback in.
      try {
         result = ((CallbackPreferringPlatformTransactionManager) ptm).execute(txAttr, status -> {
            TransactionInfo txInfo = prepareTransactionInfo(ptm, txAttr, joinpointIdentification, status);
            try {
               Object retVal = invocation.proceedWithInvocation();
               if (retVal != null && vavrPresent && VavrDelegate.isVavrTry(retVal)) {
                  // Set rollback-only in case of Vavr failure matching our rollback rules...
                  retVal = VavrDelegate.evaluateTryFailure(retVal, txAttr, status);
               }
               return retVal;
            } catch (Throwable ex) {
               if (txAttr.rollbackOn(ex)) {
                  // A RuntimeException: will lead to a rollback.
                  if (ex instanceof RuntimeException) {
                     throw (RuntimeException) ex;
                  } else {
                     throw new ThrowableHolderException(ex);
                  }
               } else {
                  // A normal return value: will lead to a commit.
                  throwableHolder.throwable = ex;
                  return null;
               }
            } finally {
               cleanupTransactionInfo(txInfo);
            }
         });
      } catch (ThrowableHolderException ex) {
         throw ex.getCause();
      } catch (TransactionSystemException ex2) {
         if (throwableHolder.throwable != null) {
            logger.error("Application exception overridden by commit exception", throwableHolder.throwable);
            ex2.initApplicationException(throwableHolder.throwable);
         }
         throw ex2;
      } catch (Throwable ex2) {
         if (throwableHolder.throwable != null) {
            logger.error("Application exception overridden by commit exception", throwableHolder.throwable);
         }
         throw ex2;
      }

      // Check result state: It might indicate a Throwable to rethrow.
      if (throwableHolder.throwable != null) {
         throw throwableHolder.throwable;
      }
      return result;
   }
}
```

接下来看到他的方法createTransactionIfNecessary会在有必要的时候创建一个新的事务。

这句话的理解就是 如果当前不存在事务的话就创建一个新的事务。如果当前配置的事务传播级别是

```java
protected TransactionInfo createTransactionIfNecessary(@Nullable PlatformTransactionManager tm,
                                          @Nullable TransactionAttribute txAttr, final String joinpointIdentification) {
       // 如果没有指定名称，则将方法标识应用为事务名称，就是之前设置的 class.method
   // If no name specified, apply method identification as transaction name.
   if (txAttr != null && txAttr.getName() == null) {
      txAttr = new DelegatingTransactionAttribute(txAttr) {
         @Override
         public String getName() {
            return joinpointIdentification;
         }
      };
   }

   TransactionStatus status = null;
   if (txAttr != null) {
      if (tm != null) {
         //获取事务
         status = tm.getTransaction(txAttr);
      } else {
         if (logger.isDebugEnabled()) {
            logger.debug("Skipping transactional joinpoint [" + joinpointIdentification +
                  "] because no transaction manager has been configured");
         }
      }
   }
   //准备事务信息
   return prepareTransactionInfo(tm, txAttr, joinpointIdentification, status);
}
```

接下来来到AbstractPlatformTransactionManager 的 getTransaction方法获得一个事务 

```java
public final TransactionStatus getTransaction(@Nullable TransactionDefinition definition)
      throws TransactionException {

   // 如果传入的definition 为null , 就从新定义一个新的definition (StaticTransactionDefinition 类型)
   TransactionDefinition def = (definition != null ? definition :                                 TransactionDefinition.withDefaults());
   //这里是获取 当前线程缓存在 threadLocal里面的 connection, 如果没有connection,那DataSourceTransactionObject 里面的 connectionHolder 为null
   Object transaction = doGetTransaction();
   boolean debugEnabled = logger.isDebugEnabled();
   // 这里时判断如果上面的 connectionHolder 不为空，并且是有效的
   if (isExistingTransaction(transaction)) {
      // Existing transaction found -> check propagation behavior to find out how to behave.
      // 这里是 为已经存在的 transaction创建一个 TransactionStatus对象
      return handleExistingTransaction(def, transaction, debugEnabled);
   }

   // Check definition settings for new transaction.
   if (def.getTimeout() < TransactionDefinition.TIMEOUT_DEFAULT) {
      throw new InvalidTimeoutException("Invalid transaction timeout", def.getTimeout());
   }
       // TODO
   // No existing transaction found -> check propagation behavior to find out how to proceed.
   if (def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_MANDATORY) {
      throw new IllegalTransactionStateException(
            "No existing transaction found for transaction marked with propagation 'mandatory'");
   }
   // 前面已经确定了不存在事务
   // TransactionDefinition.PROPAGATION_REQUIRED
   // TransactionDefinition.PROPAGATION_REQUIRES_NEW
   // TransactionDefinition.PROPAGATION_NESTED
   // 这三种情况都会生成一个新的事务来进行操作
   else if (def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED ||
         def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW ||
         def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NESTED) {
      // 这块空挂起，不做任何操作
      SuspendedResourcesHolder suspendedResources = suspend(null);
      if (debugEnabled) {
         logger.debug("Creating new transaction with name [" + def.getName() + "]: " + def);
      }
      try {
         // 这里就是开启事务
         return startTransaction(def, transaction, debugEnabled, suspendedResources);
      }
      catch (RuntimeException | Error ex) {
         resume(null, suspendedResources);
         throw ex;
      }
   }
   else {
      // Create "empty" transaction: no actual transaction, but potentially synchronization.
      if (def.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT && logger.isWarnEnabled()) {
         logger.warn("Custom isolation level specified but no actual transaction initiated; " +
               "isolation level will effectively be ignored: " + def);
      }
      boolean newSynchronization = (getTransactionSynchronization() == SYNCHRONIZATION_ALWAYS);
      //准备事务信息
      return prepareTransactionStatus(def, null, true, newSynchronization, debugEnabled, null);
   }
}


//已经存在事务的情况下 会调用这个方法去获取一个事务 这里 就是 A -> B B方式获取事务调用的方法
private TransactionStatus handleExistingTransaction(
			TransactionDefinition definition, Object transaction, boolean debugEnabled)
			throws TransactionException {
        //以非事务方式执行，如果当前存在事务，则抛出异常
		if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NEVER) {
			throw new IllegalTransactionStateException(
					"Existing transaction found for transaction marked with propagation 'never'");
		}
        // 以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。
		if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NOT_SUPPORTED) {
			if (debugEnabled) {
				logger.debug("Suspending current transaction");
			}
			Object suspendedResources = suspend(transaction);
			boolean newSynchronization = (getTransactionSynchronization() == SYNCHRONIZATION_ALWAYS);
			return prepareTransactionStatus(
					definition, null, false, newSynchronization, debugEnabled, suspendedResources);
		}
        // 创建一个新的事务
		// 因为外层此时已经有了一个事务
		// 所以会直接创建一个新的事务
		if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW) {
			if (debugEnabled) {
				logger.debug("Suspending current transaction, creating new transaction with name [" +
						definition.getName() + "]");
			}
			SuspendedResourcesHolder suspendedResources = suspend(transaction);
			try {
				//创建一个新的事务
				return startTransaction(definition, transaction, debugEnabled, suspendedResources);
			}
			catch (RuntimeException | Error beginEx) {
				resumeAfterBeginException(transaction, suspendedResources, beginEx);
				throw beginEx;
			}
		}
         // 嵌套事务的创建
		if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NESTED) {
			if (!isNestedTransactionAllowed()) {
				throw new NestedTransactionNotSupportedException(
						"Transaction manager does not allow nested transactions by default - " +
						"specify 'nestedTransactionAllowed' property with value 'true'");
			}
			if (debugEnabled) {
				logger.debug("Creating nested transaction with name [" + definition.getName() + "]");
			}
			if (useSavepointForNestedTransaction()) {
				// Create savepoint within existing Spring-managed transaction,
				// through the SavepointManager API implemented by TransactionStatus.
				// Usually uses JDBC 3.0 savepoints. Never activates Spring synchronization.
				DefaultTransactionStatus status =
						prepareTransactionStatus(definition, transaction, false, false, debugEnabled, null);
				//创建保存点
				status.createAndHoldSavepoint();
				//这个是嵌套 事务 作为主事务的子事务 所以当前的事务信息没有变化
				return status;
			}
			else {
				// Nested transaction through nested begin and commit/rollback calls.
				// Usually only for JTA: Spring synchronization might get activated here
				// in case of a pre-existing JTA transaction.
				return startTransaction(definition, transaction, debugEnabled, null);
			}
		}

		// Assumably PROPAGATION_SUPPORTS or PROPAGATION_REQUIRED.
		if (debugEnabled) {
			logger.debug("Participating in existing transaction");
		}
		if (isValidateExistingTransaction()) {
			if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
				Integer currentIsolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
				if (currentIsolationLevel == null || currentIsolationLevel != definition.getIsolationLevel()) {
					Constants isoConstants = DefaultTransactionDefinition.constants;
					throw new IllegalTransactionStateException("Participating transaction with definition [" +
							definition + "] specifies isolation level which is incompatible with existing transaction: " +
							(currentIsolationLevel != null ?
									isoConstants.toCode(currentIsolationLevel, DefaultTransactionDefinition.PREFIX_ISOLATION) :
									"(unknown)"));
				}
			}
			if (!definition.isReadOnly()) {
				if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
					throw new IllegalTransactionStateException("Participating transaction with definition [" +
							definition + "] is not marked as read-only but existing transaction is");
				}
			}
		}
		boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
         //其余三种事务传播级别都是支持当前事务 去进行操作 
		//因为进入这个方法的时候当前必定有一个事务 
         //所以对于PROPAGATION_REQUIRED PROPAGATION_SUPPORTS  PROPAGATION_MANDATORY 这三种情况来说都是一样的
		return prepareTransactionStatus(definition, transaction, false, newSynchronization, debugEnabled, null);
	}

```

事务创建完成后就到了方法的执行 现在回到 TransactionAspectSupport 的 invokeWithinTransaction 方法里面一个类似@Around的地方

```java
if (txAttr == null || !(ptm instanceof CallbackPreferringPlatformTransactionManager)) {
   // Standard transaction demarcation with getTransaction and commit/rollback calls.
   // 有必要时创建一个事务
   TransactionInfo txInfo = createTransactionIfNecessary(ptm, txAttr, joinpointIdentification);

   Object retVal;
   //这个相当于是一个around advice
   // invocation.proceedWithInvocation() 进行方法的执行
   // completeTransactionAfterThrowing(txInfo, ex); 进行异常的回滚 或者 非拦截的异常的事务的提交
   // cleanupTransactionInfo 还原线程状态
   try {
      // This is an around advice: Invoke the next interceptor in the chain.
      // This will normally result in a target object being invoked.
      retVal = invocation.proceedWithInvocation();
   } catch (Throwable ex) {
      // 异常事务回滚 或者非指定异常事务提交
      completeTransactionAfterThrowing(txInfo, ex);
      throw ex;
   } finally {
      cleanupTransactionInfo(txInfo);
   }

   if (retVal != null && vavrPresent && VavrDelegate.isVavrTry(retVal)) {
      // Set rollback-only in case of Vavr failure matching our rollback rules...
      TransactionStatus status = txInfo.getTransactionStatus();
      if (status != null && txAttr != null) {
         retVal = VavrDelegate.evaluateTryFailure(retVal, txAttr, status);
      }
   }
          //提交事务
   commitTransactionAfterReturning(txInfo);
   return retVal;
}


//事务回滚的操作
protected void completeTransactionAfterThrowing(@Nullable TransactionInfo txInfo, Throwable ex) {
		if (txInfo != null && txInfo.getTransactionStatus() != null) {
			// rollbackOn 判断该异常是否符合事务回滚的异常
			if (txInfo.transactionAttribute != null && txInfo.transactionAttribute.rollbackOn(ex)) {
				try {
					txInfo.getTransactionManager().rollback(txInfo.getTransactionStatus());
				}
				catch (TransactionSystemException ex2) {
					ex2.initApplicationException(ex);
					throw ex2;
				}
				catch (RuntimeException | Error ex2) {
					throw ex2;
				}
			}
			else {
				// We don't roll back on this exception.
				// Will still roll back if TransactionStatus.isRollbackOnly() is true.
				try {
					txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());
				}
				catch (TransactionSystemException ex2) {
					ex2.initApplicationException(ex);
					throw ex2;
				}
				catch (RuntimeException | Error ex2) {
					throw ex2;
				}
			}
		}
	}


private void processRollback(DefaultTransactionStatus status, boolean unexpected) {
		try {
			boolean unexpectedRollback = unexpected;

			try {
				triggerBeforeCompletion(status);
                //当前事务是否有设置保存点
				if (status.hasSavepoint()) {
					//事务回退到保存点
					status.rollbackToHeldSavepoint();
				}
				//当前事务是一个新事务
				else if (status.isNewTransaction()) {
					// 回退当前事务
					doRollback(status);
				}
				else {
					// Participating in larger transaction
					if (status.hasTransaction()) {
						if (status.isLocalRollbackOnly() || isGlobalRollbackOnParticipationFailure()) {
							//事物对象设置为只可以回滚，不可提交
                               // 与他公用一个事务的操作就会报错
							doSetRollbackOnly(status);
						}
					}
					// Unexpected rollback only matters here if we're asked to fail early
					if (!isFailEarlyOnGlobalRollbackOnly()) {
						unexpectedRollback = false;
					}
				}
			}
			catch (RuntimeException | Error ex) {
				triggerAfterCompletion(status, TransactionSynchronization.STATUS_UNKNOWN);
				throw ex;
			}

			triggerAfterCompletion(status, TransactionSynchronization.STATUS_ROLLED_BACK);
		
		}
		finally {
			cleanupAfterCompletion(status);
		}
	}







```

事务提交的时候的commit方法

```java
public final void commit(TransactionStatus status) throws TransactionException {
   if (status.isCompleted()) {
      throw new IllegalTransactionStateException(
            "Transaction is already completed - do not call commit or rollback more than once per transaction");
   }

   DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
   if (defStatus.isLocalRollbackOnly()) {
      if (defStatus.isDebug()) {
         logger.debug("Transactional code has requested rollback");
      }
      processRollback(defStatus, false);
      return;
   }
   // 事务状态设置为仅仅可以回退的时候此时还是对事务进行回退操作  而不是提交
   if (!shouldCommitOnGlobalRollbackOnly() && defStatus.isGlobalRollbackOnly()) {
      if (defStatus.isDebug()) {
         logger.debug("Global transaction is marked as rollback-only but transactional code requested commit");
      }
      //此时 true这个入参会导致后续 回滚完成之后进行报错
      //造成意外回滚
      processRollback(defStatus, true);
      return;
   }

   processCommit(defStatus);
}
```