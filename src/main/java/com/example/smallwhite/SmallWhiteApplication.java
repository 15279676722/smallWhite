package com.example.smallwhite;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author yangqiang
 */
@EnableCaching
//事务处理
@EnableTransactionManagement
@SpringBootApplication
@MapperScan("com.example.smallwhite.mybatis.dao")
public class SmallWhiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmallWhiteApplication.class, args);
    }

}
