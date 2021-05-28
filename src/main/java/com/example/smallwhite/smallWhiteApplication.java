package com.example.smallwhite;

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
//@MapperScan({"com.example.babysrj.mapper","com.example.babysrj.mybatis"})
public class smallWhiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(smallWhiteApplication.class, args);
    }

}
