package com.example.smallwhite;import lombok.extern.slf4j.Slf4j;import org.junit.jupiter.api.Test;import org.redisson.api.RLock;import org.redisson.api.RedissonClient;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.boot.test.context.SpringBootTest;import java.util.concurrent.TimeUnit;/** * @author: yangqiang * @create: 2021-03-13 18:07 */@SpringBootTest@Slf4jpublic class RedissonReentrantLockTest {    @Autowired    private RedissonClient redissonClient;    @Test    public void test() throws InterruptedException {        RLock lock = redissonClient.getLock("test");        lock.lock(5, TimeUnit.SECONDS);        // 拿锁失败时会不停的重试        lock.tryLock(5,TimeUnit.SECONDS);        lock.unlock();    }}