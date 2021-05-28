package com.example.smallwhite.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author: yangqiang
 * @create: 2020-04-03 11:14
 */
@Configuration
@EnableAspectJAutoProxy
public class Cap10MainConfig {
    @Bean
    public Calculator   calculator(){
        return new Calculator();
    }

    @Bean
    public LogAspects logAspects(){
        return new LogAspects();
    }
    @Bean
    public TestAspects testAspects(){
        return new TestAspects();
    }
    @Bean
    public EncoreableIntroducer encoreableIntroducer(){
        return new EncoreableIntroducer();
    }
}
