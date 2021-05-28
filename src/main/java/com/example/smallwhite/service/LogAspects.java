package com.example.smallwhite.service;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Arrays;
import java.util.List;

/**
 * @author: yangqiang
 * @create: 2020-04-03 13:47
 */
//日志切面类
@Aspect
public class LogAspects {
    @Pointcut("execution(public int com.example.smallwhite.service.Calculator.div(..))")
    public void pointCut(){};

    /**
     * @Before: 代表在目标方法执行前切入, 并指定在哪个方法前切入
     */
    @Before("pointCut()")
    public void logStart(JoinPoint point){
        String methodName = point.getSignature().getName();
        List<Object> args = Arrays.asList(point.getArgs());
        System.out.println("调用前连接点方法为：" + methodName + ",参数为：" + args);
    }
    @After("pointCut()")
    public void logEnd(){
        System.out.println("除法结束......");
    }
    @AfterReturning("pointCut()")
    public void logReturn(){
        System.out.println("除法正常返回......运行结果是:{}");
    }
    @AfterThrowing("pointCut()")
    public void logException(){
        System.out.println("运行异常......异常信息是:{}");
    }
    /**
     * Around 环绕通知 环绕通知可以包括上述四种的通知方法 环绕通知的功能更全面
     * 环绕通知需要携带 ProceedingJoinPoint 类型的参数，且环绕通知必须有返回值, 返回值即为目标方法的返回值。在切面类中创建环绕通知方法，示例如下：
     *   1.执行proceed方法之前的相当于是 @Before
     *   2.执行proceed方法之后的相当于是 @After
     *   3.执行proceed方法发生异常相当于是 @AfterThrowing
     *   4.执行proceed方法正常返回相当于是@AfterReturning
     * */
    @Around("pointCut()")
    public Object Around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        System.out.println("@Around:执行目标方法之前...");
        Object obj = null;
        try {
            //相当于开始调用div方法
            obj = proceedingJoinPoint.proceed();
        } catch (Exception e){
            System.out.println("运行异常......异常信息是:"+e.getMessage());
        }
        System.out.println("@Around:执行目标方法之后...");
        return obj;
    }
}
