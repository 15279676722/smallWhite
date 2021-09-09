//package com.example.smallwhite.utils;////import com.example.smallwhite.entity.RedisKeyExpVO;//import org.springframework.beans.factory.annotation.Autowired;//import org.springframework.data.redis.connection.Message;//import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;//import org.springframework.data.redis.listener.RedisMessageListenerContainer;//import org.springframework.stereotype.Component;////import java.sql.Timestamp;//import java.util.ArrayList;//import java.util.List;//import java.util.UUID;/////**// * 监听redis key的过期 每个key过期后会调用onMessage// * @author: yangqiang// * @create: 2021-01-19 13:42// *///@Component//public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {//    private volatile Integer count = 0;//    private List<RedisKeyExpVO> list = new ArrayList<>();//    private final Integer NUM = 10000;//    @Autowired//    JdbcUtil jdbcUtil;//    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {//        super(listenerContainer);//    }////    /**//     * 针对 redis 数据失效事件，进行数据处理//     * @param message//     * @param pattern//     *///    @Override//    public void onMessage(Message message, byte[] pattern) {//        // 获取到失效的 key，进行取消订单业务处理//        String expiredKey = message.toString();//        count++;//        RedisKeyExpVO redisKeyExpVO = new RedisKeyExpVO(UUID.randomUUID().toString(), expiredKey, new Timestamp(System.currentTimeMillis()),count);//        list.add(redisKeyExpVO);//        if(list.size() >= NUM){//            jdbcUtil.insert(list);//            list.clear();//        }//    }//}